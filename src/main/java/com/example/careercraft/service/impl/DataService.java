package com.example.careercraft.service.impl;

import com.example.careercraft.dto.QuestionIdsDto;
import com.example.careercraft.dto.SkillDTO;
import com.example.careercraft.entity.Question;
import com.example.careercraft.service.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DataService {

    private final SkillService skillService;
    private final JobService jobService;
    private final QuestionService questionService; // Сервис для получения вопросов и ответов
    private final AnswerService answerService; // Сервис для получения ответов
    private final ReportService reportService; // Сервис для получения reportId

    @Transactional
    public Map<String, Object> getInitialData() {
        Map<String, Object> response = new HashMap<>();

        // Получаем DTO для всех навыков
        Collection<SkillDTO> skillDTOs = skillService.getAllSkills();

        // Извлекаем идентификаторы навыков из DTO
        List<Long> skillIds = skillDTOs.stream()
                .map(SkillDTO::getId)
                .collect(Collectors.toList());

        // Получаем список всех доступных идентификаторов работ
        Collection<Long> jobIds = jobService.getAllJobIds();

        // Получаем значение id (если нужно)
        Long id = null; // Удалите или замените на реальное значение, если требуется

        // Получаем список вопросов и ответов
        List<QuestionIdsDto> questionAnswerDTOs = questionService.getAllQuestions();

        // Получаем reportId на основе идентификаторов навыков
        Long reportId = reportService.getReportId(skillIds);

        // Заполняем ответ
        response.put("skills", skillDTOs); // Добавляем DTO для навыков
        response.put("jobIds", jobIds);
        response.put("id", null);
        response.put("questionAnswers", questionAnswerDTOs);
        response.put("reportId", reportId);

        return response;
    }
}
