package com.example.careercraft.service.impl;

import com.example.careercraft.dto.*;
import com.example.careercraft.entity.*;
import com.example.careercraft.exception.AlreadyExistException;
import com.example.careercraft.exception.IncompleteAnswersException;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.*;
import com.example.careercraft.service.AuthService;
import com.example.careercraft.service.CategoryService;
import com.example.careercraft.service.ReportService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {


    private final SkillRepository skillRepository;

    private final AuthService authService;


    private final UserAnswerRepository userAnswerRepository;


    private final ReportRepository reportRepository;


    private final QuestionRepository questionRepository;


    private final CustomerFinderService customerFinderService;

    private final CategoryRepository categoryRepository;
    private final AggregatedReportRepository aggregatedReportRepository;
    private final CategoryService categoryService;



    @Override
    public List<AggregatedReportDto> generateReportForSkills(Long customerId, Long categoryId) {
        Customer customer = customerFinderService.getCustomer(customerId);
        List<Skill> skillsInCategory = skillRepository.findByCategoryId(categoryId);
        List<UserAnswer> userAnswers = userAnswerRepository.findByCustomerId(customerId);
        Map<Long, List<UserAnswer>> answersBySkill = groupAnswersBySkill(userAnswers);
        checkAllQuestionsAnswered(skillsInCategory, answersBySkill, categoryId);
        List<ReportDto> individualReports = skillsInCategory.stream()
                .map(skill -> generateReportForSkill(customerId, skill.getId(), answersBySkill.get(skill.getId()), categoryId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        // Агрегируем отчеты по категориям
        List<AggregatedReportDto> aggregatedReports = aggregateReportsByCategory(individualReports, customerId);

        // Сохраняем агрегированные отчеты
        saveAggregatedReports(aggregatedReports);

        return aggregatedReports;
    }

    public AggregatedReportDto getAggregatedReportForCategory(String authHeader, Long categoryId) {
        CustomerInfo customerInfo = authService.getCustomerDetailsFromToken(authHeader);
        AggregatedReport aggregatedReport = aggregatedReportRepository.findByCustomerIdAndCategoryIdAndValid(customerInfo.getId(), categoryId, true)
                .orElseThrow(() -> new NotFoundException("Valid aggregated report not found for customerId: " + customerInfo.getId() + " and categoryId: " + categoryId));

        return convertToAggregatedReportDto(aggregatedReport);
    }


    private void checkAllQuestionsAnswered(List<Skill> skillsInCategory, Map<Long, List<UserAnswer>> answersBySkill, Long categoryId) {
        for (Skill skill : skillsInCategory) {
            Long skillId = skill.getId();
            List<UserAnswer> answers = answersBySkill.get(skillId);

            if (answers == null || answers.size() < countTotalQuestionsForSkill(skill)) {
                throw new IncompleteAnswersException("Not all questions answered for skillId: " + skillId + " in categoryId: " + categoryId);
            }
        }
    }

    @Override
    public SkillReportDto getDetailedReportForSkill(String authHeader, Long skillId) {
        // Получение информации о пользователе на основе токена
        CustomerInfo customerInfo = authService.getCustomerDetailsFromToken(authHeader);

        // Поиск детализированного отчета по скиллу
        Report report = reportRepository.findByCustomerIdAndSkillIdAndValid(customerInfo.getId(), skillId, true)
                .orElseThrow(() -> new NotFoundException("Valid detailed report not found for customerId: "
                        + customerInfo.getId() + " and skillId: " + skillId));

        // Преобразование отчета в DTO
        return convertToSkillReportDto(report);
    }

    public List<ReportDto> getAllReportsForCategoryAndCustomer(String authHeader, Long categoryId) {
        // Получение информации о пользователе на основе токена
        CustomerInfo customerInfo = authService.getCustomerDetailsFromToken(authHeader);

        // Поиск всех отчетов для определенной категории и пользователя
        List<Report> reports = reportRepository.findAllByCustomerIdAndCategoryIdAndValid(customerInfo.getId(), categoryId, true);

        // Преобразование списка отчетов в DTO
        return reports.stream()
                .map(this::convertToReportDto)
                .collect(Collectors.toList());
    }

    private ReportDto convertToReportDto(Report report) {
        ReportDto reportDto = new ReportDto();

        // Заполняем поля DTO из сущности Report
        reportDto.setReportId(report.getId());
        reportDto.setCustomerId(report.getCustomer().getId());
        reportDto.setCategoryId(report.getCategory().getId());
        reportDto.setCategoryName(report.getCategory().getName());
        reportDto.setSkillId(report.getSkill() != null ? report.getSkill().getId() : null);
        reportDto.setSkillName(report.getSkill() != null ? report.getSkill().getName() : null);
        reportDto.setScore(report.getScore());
        reportDto.setPercentageCorrect(report.getPercentageCorrect());
        reportDto.setSkillLevel(report.getSkillLevel());
        reportDto.setValid(report.isValid());




        return reportDto;
    }

    private SkillReportDto convertToSkillReportDto(Report report) {
        SkillReportDto dto = new SkillReportDto();
        dto.setReportId(report.getId());
        dto.setCustomerId(report.getCustomer().getId());
        dto.setSkillId(report.getSkill().getId());
        dto.setSkillName(report.getSkill().getName()); // Устанавливаем название скилла
        dto.setScore(report.getScore());
        dto.setPercentageCorrect(report.getPercentageCorrect());
        dto.setSkillLevel(report.getSkillLevel());
        dto.setValid(report.isValid());
        return dto;
    }

    private Map<Long, List<UserAnswer>> groupAnswersBySkill(List<UserAnswer> userAnswers) {
        return userAnswers.stream()
                .collect(Collectors.groupingBy(answer -> answer.getSkill().getId()));
    }

    private Optional<ReportDto> generateReportForSkill(Long customerId, Long skillId, List<UserAnswer> answersForSkill, Long categoryId) {
        try {
            Skill skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new NotFoundException("Skill not found with id: " + skillId));
            log.info("Generating report for customerId: {}, skillId: {}, categoryId: {}", customerId, skillId, categoryId);
            invalidateExistingReportsForSkill(customerId, skillId, categoryId);

            if (answersForSkill == null || answersForSkill.size() < countTotalQuestionsForSkill(skill)) {
                throw new IncompleteAnswersException("Not all questions answered for skillId: " + skillId);
            }

            Report report = createReport(customerId, answersForSkill, skill, categoryId);
            log.info("Report created: {}", report);
            reportRepository.save(report);
            return Optional.of(convertToReportDto(report, answersForSkill));
        } catch (IncompleteAnswersException e) {
            log.warn("Incomplete answers for skillId: {}, categoryId: {}, customerId: {}: {}", skillId, categoryId, customerId, e.getMessage());
            return Optional.empty();
        } catch (IllegalArgumentException | AlreadyExistException e) {
            log.warn("Error generating report for skillId: {}, categoryId: {}, customerId: {}: {}", skillId, categoryId, customerId, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error generating report for skillId: {}, categoryId: {}, customerId: {}", skillId, categoryId, customerId, e);
            throw e;
        }
    }

    private Report createReport(Long customerId, List<UserAnswer> userAnswers, Skill skill, Long categoryId) {
        log.info("Creating report with customerId: {}, skillId: {}, categoryId: {}", customerId, skill.getId(), categoryId);

        BigDecimal totalScore = calculateTotalScore(userAnswers);
        long totalQuestionsForSkill = countTotalQuestionsForSkill(skill);
        double percentageCorrect = calculatePercentageCorrect(totalScore, totalQuestionsForSkill);
        SkillLevel skillLevel = determineSkillLevel(percentageCorrect);
        Customer customer = customerFinderService.getCustomer(customerId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId));

        Report report = buildReport(customer, userAnswers, skill, totalScore, percentageCorrect, skillLevel, category);
        report.setValid(true);
        log.info("Report built: {}", report);

        return report;
    }

    private void invalidateExistingReportsForSkill(Long customerId, Long skillId, Long categoryId) {
        List<Report> existingReports = reportRepository.findByCustomerIdAndSkillIdAndCategoryId(customerId, skillId, categoryId);
        for (Report report : existingReports) {
            report.setValid(false);
        }
        reportRepository.saveAll(existingReports);
    }

    private Report buildReport(Customer customer, List<UserAnswer> userAnswers, Skill skill,
                               BigDecimal totalScore, double percentageCorrect, SkillLevel skillLevel, Category category) {
        Report report = new Report();
        report.setCustomer(customer);
        report.setJob(userAnswers.get(0).getQuestion().getJob());
        report.setSkill(skill);
        report.setScore(totalScore);
        report.setPercentageCorrect(percentageCorrect);
        report.setSkillLevel(skillLevel);
        report.setUserAnswers(userAnswers);
        report.setCategory(category); // Устанавливаем категорию
        return report;
    }

    private AggregatedReportDto createAggregatedReport(Long categoryId, List<ReportDto> reports, Long customerId) {
        AggregatedReportDto aggregatedReport = new AggregatedReportDto();
        aggregatedReport.setCategoryId(categoryId);
        aggregatedReport.setCustomerId(customerId);
        String categoryName = reports.get(0).getCategoryName();
        aggregatedReport.setCategoryName(categoryName);
        BigDecimal totalScore = reports.stream()
                .map(ReportDto::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        aggregatedReport.setTotalScore(totalScore.doubleValue());

        double averagePercentageCorrect = reports.stream()
                .mapToDouble(ReportDto::getPercentageCorrect)
                .average()
                .orElse(0.0);

        // Округление averagePercentageCorrect до двух знаков после запятой
        BigDecimal roundedAveragePercentageCorrect = BigDecimal.valueOf(averagePercentageCorrect)
                .setScale(2, RoundingMode.HALF_UP);
        aggregatedReport.setAveragePercentageCorrect(roundedAveragePercentageCorrect.doubleValue());

        String averageSkillLevel = determineAverageSkillLevel(reports);
        aggregatedReport.setSkillLevel(averageSkillLevel);

        return aggregatedReport;
    }

    private BigDecimal calculateTotalScore(List<UserAnswer> userAnswers) {
        return userAnswers.stream()
                .map(userAnswer -> BigDecimal.valueOf(userAnswer.getAnswer().getScore()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private long countTotalQuestionsForSkill(Skill skill) {
        return questionRepository.countBySkillId(skill.getId());
    }

    private double calculatePercentageCorrect(BigDecimal totalScore, long totalQuestionsForSkill) {
        BigDecimal maxPossibleScore = BigDecimal.valueOf(totalQuestionsForSkill * 3);
        BigDecimal percentage = totalScore.divide(maxPossibleScore, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return percentage.doubleValue();
    }

    private SkillLevel determineSkillLevel(double percentageCorrect) {
        String level = switch ((int) percentageCorrect / 10) {
            case 8, 9 -> "ADVANCED";
            case 6, 7 -> "INTERMEDIATE";
            default -> "BEGINNER";
        };

        return SkillLevel.valueOf(level);
    }

    private final Map<SkillLevel, Integer> skillLevelMap = Map.of(
            SkillLevel.ADVANCED, 3,
            SkillLevel.INTERMEDIATE, 2,
            SkillLevel.BEGINNER, 1
    );

    private String determineAverageSkillLevel(List<ReportDto> reports) {
        double averageLevel = reports.stream()
                .mapToInt(report -> skillLevelMap.getOrDefault(SkillLevel.valueOf(String.valueOf(report.getSkillLevel())), 1))
                .average()
                .orElse(1);

        return skillLevelMap.entrySet().stream()
                .filter(entry -> entry.getValue() == (int) Math.round(averageLevel))
                .map(Map.Entry::getKey)
                .map(SkillLevel::name)
                .findFirst()
                .orElse("BEGINNER");
    }

    private ReportDto convertToReportDto(Report report, List<UserAnswer> userAnswers) {
        ReportDto reportDto = new ReportDto();
        reportDto.setCustomerId(report.getCustomer().getId());
        reportDto.setScore(report.getScore());
        reportDto.setPercentageCorrect(report.getPercentageCorrect());
        reportDto.setSkillLevel(SkillLevel.valueOf(report.getSkillLevel().name()));

        if (report.getSkill() != null) {
            reportDto.setSkillId(report.getSkill().getId());
            reportDto.setSkillName(report.getSkill().getName());
        }

        reportDto.setCategoryId(report.getCategory().getId());
        reportDto.setCategoryName(report.getCategory().getName());

        return reportDto;
    }

    private List<AggregatedReportDto> aggregateReportsByCategory(List<ReportDto> individualReports, Long customerId) {
        Map<Long, List<ReportDto>> reportsByCategory = individualReports.stream()
                .collect(Collectors.groupingBy(ReportDto::getCategoryId));

        return reportsByCategory.entrySet().stream()
                .map(entry -> createAggregatedReport(entry.getKey(), entry.getValue(), customerId))
                .collect(Collectors.toList());
    }

    private void saveAggregatedReports(List<AggregatedReportDto> aggregatedReportsDto) {
        List<AggregatedReport> aggregatedReports = aggregatedReportsDto.stream()
                .map(dto -> {
                    AggregatedReport report = new AggregatedReport();
                    report.setCustomerId(dto.getCustomerId());
                    report.setCategoryId(dto.getCategoryId());
                    report.setTotalScore(dto.getTotalScore());
                    report.setCategoryName(dto.getCategoryName());
                    report.setAveragePercentageCorrect(dto.getAveragePercentageCorrect());
                    report.setSkillLevel(dto.getSkillLevel());
                    report.setValid(true); // устанавливаем как действительный
                    return report;
                })
                .collect(Collectors.toList());

        // Обновление старых отчетов как недействительных
        invalidateExistingReports(aggregatedReportsDto);

        aggregatedReportRepository.saveAll(aggregatedReports);
    }

    private void invalidateExistingReports(List<AggregatedReportDto> newReports) {
        for (AggregatedReportDto newReport : newReports) {
            List<AggregatedReport> existingReports = aggregatedReportRepository.findByCustomerIdAndCategoryId(newReport.getCustomerId(), newReport.getCategoryId());
            for (AggregatedReport report : existingReports) {
                report.setValid(false);
            }
            aggregatedReportRepository.saveAll(existingReports);
        }
    }



    private AggregatedReportDto convertToAggregatedReportDto(AggregatedReport aggregatedReport) {
        AggregatedReportDto dto = new AggregatedReportDto();
        dto.setCustomerId(aggregatedReport.getCustomerId());
        Category category = categoryService.findById(aggregatedReport.getCategoryId());
        dto.setCategoryName(category.getName());
        dto.setCategoryId(aggregatedReport.getCategoryId());
        dto.setTotalScore(aggregatedReport.getTotalScore());
        dto.setAveragePercentageCorrect(aggregatedReport.getAveragePercentageCorrect());
        dto.setSkillLevel(aggregatedReport.getSkillLevel());
        return dto;
    }



    public Long getReportId(List<Long> skillIds) {
        // Получаем список отчетов для заданных skillIds
        List<Long> reportIds = skillRepository.findReportIdsBySkillIds(skillIds);

        // Логика для выбора одного reportId из списка, например, берем первое
        return reportIds.isEmpty() ? null : reportIds.get(0);
    }

}

