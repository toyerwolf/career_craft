package com.example.careercraft.service.impl;

import com.example.careercraft.dto.AggregatedReportDto;
import com.example.careercraft.dto.CustomerInfo;
import com.example.careercraft.dto.QuestionAnswerDto;
import com.example.careercraft.dto.ReportDto;
import com.example.careercraft.entity.*;
import com.example.careercraft.exception.AlreadyExistException;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.*;
import com.example.careercraft.service.AuthService;
import com.example.careercraft.service.ReportService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {


    private SkillRepository skillRepository;

    private final AuthService authService;


    private UserAnswerRepository userAnswerRepository;


    private ReportRepository reportRepository;


    private QuestionRepository questionRepository;


    private CustomerFinderService customerFinderService;

    private CategoryRepository categoryRepository;
    private final AggregatedReportRepository aggregatedReportRepository;

    @Override
    public List<AggregatedReportDto> generateReportForSkills(Long customerId, Long categoryId) {
        Customer customer = customerFinderService.getCustomer(customerId);
        List<Skill> skillsInCategory = skillRepository.findByCategoryId(categoryId);
        List<UserAnswer> userAnswers = userAnswerRepository.findByCustomerId(customerId);
        Map<Long, List<UserAnswer>> answersBySkill = groupAnswersBySkill(userAnswers);

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
            Report report = createReport(customerId, answersForSkill, skill, categoryId);
            log.info("Report created: {}", report);
            reportRepository.save(report);
            return Optional.of(convertToReportDto(report, answersForSkill));
        } catch (AlreadyExistException e) {
            log.warn("Report already exists for skillId: {}, categoryId: {}, customerId: {}", skillId, categoryId, customerId);
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
            case 9 -> "EXPERT";
            case 8, 7 -> "ADVANCED";
            case 6, 5 -> "INTERMEDIATE";
            default -> "BEGINNER";
        };

        return SkillLevel.valueOf(level);
    }

    private final Map<SkillLevel, Integer> skillLevelMap = Map.of(
            SkillLevel.EXPERT, 4,
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

