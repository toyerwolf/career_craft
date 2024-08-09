package com.example.careercraft.service.impl;

import com.example.careercraft.dto.QuestionAnswerDto;
import com.example.careercraft.dto.ReportDto;
import com.example.careercraft.entity.*;
import com.example.careercraft.exception.AlreadyExistException;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.*;
import com.example.careercraft.service.ReportService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;
    private final ReportRepository reportRepository;
    private final SkillRepository skillRepository;
    private final CustomerFinderService customerFinderService;
    @Transactional
    @Override
    public List<ReportDto> generateReportForSkills(Long customerId, List<Long> skillIds) {
        Customer customer = customerFinderService.getCustomer(customerId);
        List<UserAnswer> userAnswers = userAnswerRepository.findByCustomerId(customerId);
        Map<Long, List<UserAnswer>> answersBySkill = groupAnswersBySkill(userAnswers);

        return skillIds.stream()
                .filter(answersBySkill::containsKey)
                .map(skillId -> generateReportForSkill(customerId, skillId, answersBySkill.get(skillId)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Map<Long, List<UserAnswer>> groupAnswersBySkill(List<UserAnswer> userAnswers) {
        return userAnswers.stream()
                .collect(Collectors.groupingBy(answer -> answer.getSkill().getId()));
    }

    private Skill getSkillById(Long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new NotFoundException("Skill not found with id: " + skillId));
    }

    private Optional<ReportDto> generateReportForSkill(Long customerId, Long skillId, List<UserAnswer> answersForSkill) {
        try {
            Skill skill = getSkillById(skillId);
//            ensureReportDoesNotExist(customerId, skillId);
            Report report = createReport(customerId, answersForSkill, skill);
            reportRepository.save(report);
            return Optional.of(convertToReportDto(report, answersForSkill));
        } catch (AlreadyExistException e) {
            // Log or handle exception if needed
            return Optional.empty();
        }
    }

//    private void ensureReportDoesNotExist(Long customerId, Long skillId) {
//        if (reportRepository.existsByCustomerIdAndSkillId(customerId, skillId)) {
//            throw new AlreadyExistException("Report for skill ID " + skillId + " already exists for customer ID " + customerId);
//        }
//    }

    private Report createReport(Long customerId, List<UserAnswer> userAnswers, Skill skill) {
        BigDecimal totalScore = calculateTotalScore(userAnswers);
        long totalQuestionsForSkill = countTotalQuestionsForSkill(skill);
        double percentageCorrect = calculatePercentageCorrect(totalScore, totalQuestionsForSkill);
        SkillLevel skillLevel = determineSkillLevel(percentageCorrect);
        Customer customer = customerFinderService.getCustomer(customerId);

        return buildReport(customer, userAnswers, skill, totalScore, percentageCorrect, skillLevel);
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

    private Report buildReport(Customer customer, List<UserAnswer> userAnswers, Skill skill,
                               BigDecimal totalScore, double percentageCorrect, SkillLevel skillLevel) {
        Report report = new Report();
        report.setCustomer(customer);
        report.setJob(userAnswers.get(0).getQuestion().getJob());
        report.setSkill(skill);
        report.setScore(totalScore);
        report.setPercentageCorrect(percentageCorrect);
        report.setSkillLevel(skillLevel);
        report.setUserAnswers(userAnswers);
        return report;
    }

    private ReportDto convertToReportDto(Report report, List<UserAnswer> userAnswers) {
        ReportDto reportDto = new ReportDto();
        reportDto.setCustomerId(report.getCustomer().getId());
        reportDto.setScore(report.getScore());
        reportDto.setPercentageCorrect(report.getPercentageCorrect());
        reportDto.setSkillLevel(report.getSkillLevel());

        if (report.getSkill() != null) {
            reportDto.setSkillId(report.getSkill().getId());
            reportDto.setSkillName(report.getSkill().getName());
        }

        // Optionally include question answers if needed
        // List<QuestionAnswerDto> questionAnswerDtos = getQuestionAnswerDtos(userAnswers);
        // reportDto.setQuestionAnswers(questionAnswerDtos);

        return reportDto;
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


    public Long getReportId(List<Long> skillIds) {
        // Получаем список отчетов для заданных skillIds
        List<Long> reportIds = skillRepository.findReportIdsBySkillIds(skillIds);

        // Логика для выбора одного reportId из списка, например, берем первое
        return reportIds.isEmpty() ? null : reportIds.get(0);
    }

}

