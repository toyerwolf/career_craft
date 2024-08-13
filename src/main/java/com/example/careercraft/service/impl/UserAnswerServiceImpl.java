package com.example.careercraft.service.impl;

import com.example.careercraft.dto.*;

import com.example.careercraft.entity.*;
import com.example.careercraft.exception.AlreadyExistException;
import com.example.careercraft.exception.AppException;
import com.example.careercraft.exception.NotFoundException;

import com.example.careercraft.mapper.QuestionResponseMapper;
import com.example.careercraft.repository.*;
import com.example.careercraft.req.UserAnswerRequest;


import com.example.careercraft.response.AnswerResponse;
import com.example.careercraft.response.QuestionResponse;
import com.example.careercraft.response.QuestionWithReportsResponse;
import com.example.careercraft.service.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserAnswerServiceImpl implements UserAnswerService {

    private final UserAnswerRepository userAnswerRepository;


    private final CustomerFinderService customerFinderService;


    private final QuestionRepository questionRepository;


    private final QuestionService questionService;
    private final ReportService reportService;
    private final AnswerService answerService;
    private final AuthService authService;
    private final SkillRepository skillRepository;


    @Transactional
    @Override
    public QuestionResponse saveUserAnswerAndGetNextQuestion(String authHeader, UserAnswerRequest userAnswerRequest) {
        // Извлечение информации о клиенте из токена
        CustomerInfo customerInfo = extractCustomerInfo(authHeader);

        // Получение клиента и вопроса
        Customer customer = getCustomer(customerInfo.getId());
        Question question = getQuestion(userAnswerRequest.getQuestionId());

        // Обработка ответа пользователя
        Answer answer = getAnswerByOrderValue(question, userAnswerRequest.getOrderValue());
        Skill skill = getSkill(question);
        Category category = getCategory(skill); // Получение категории из навыка

        handleUserAnswer(customer, question, answer, skill);

        // Проверка, остались ли вопросы в категории
        if (areQuestionsRemainingInCategory(customer, category)) {
            // Если есть оставшиеся вопросы, найти следующий вопрос
            QuestionResponse nextQuestion = findNextQuestionInCategory(customer, category);
            return buildQuestionResponse(nextQuestion);
        } else {
            // Если вопросов больше нет, возвращаем сообщение о завершении теста
            return QuestionResponse.builder()
                    .message("Test Completed for Category!")
                    .build();
        }
    }

    private QuestionResponse findNextQuestionInCategory(Customer customer, Category category) {
        // Получаем все навыки категории
        List<Skill> skillsInCategory = skillRepository.findByCategoryId(category.getId());

        // Получаем все вопросы по всем навыкам в категории
        List<Question> questionsInCategory = skillsInCategory.stream()
                .flatMap(skill -> questionRepository.findBySkillId(skill.getId()).stream())
                .distinct()
                .toList();

        // Найти первый вопрос из списка, который еще не был задан пользователю
        Optional<Question> nextQuestion = questionsInCategory.stream()
                .filter(q -> !userAnswerRepository.existsByCustomerIdAndQuestionId(customer.getId(), q.getId())) // Исключаем уже отвеченные вопросы
                .findFirst(); // Найти первый оставшийся вопрос

        // Формирование ответа
        if (nextQuestion.isPresent()) {
            Question q = nextQuestion.get();
            // Возвращаем вопрос в формате QuestionResponse
            return buildQuestionResponse(q);
        } else {
            // Если вопросов больше нет, возвращаем сообщение о завершении теста
            return getTestCompletionResponse();

        }
    }

    private QuestionResponse getTestCompletionResponse() {
        return QuestionResponse.builder()
                .message("Test Completed for Category!")
                .build();
    }

    private boolean areQuestionsRemainingInCategory(Customer customer, Category category) {
        // Получить все навыки категории
        List<Skill> skillsInCategory = skillRepository.findByCategoryId(category.getId());

        // Проверить наличие вопросов по всем навыкам
        for (Skill skill : skillsInCategory) {
            List<Question> questionsForSkill = questionRepository.findBySkillId(skill.getId());
            // Проверяем, есть ли вопросы, которые еще не были заданы
            if (questionsForSkill.stream().anyMatch(q -> !userAnswerRepository.existsByCustomerIdAndQuestionId(customer.getId(), q.getId()))) {
                return true; // Есть оставшиеся вопросы
            }
        }
        return false; // Вопросов больше нет
    }

    private Category getCategory(Skill skill) {
        return Optional.ofNullable(skill.getCategory())
                .orElseThrow(() -> new NotFoundException("No category found for the skill"));
    }

    private QuestionResponse buildQuestionResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .text(question.getText())
                .answers(question.getAnswers().stream()
                        .map(answer -> AnswerResponse.builder()
                                .id(answer.getId())
                                .text(answer.getText())
                                .score(answer.getScore()).
                                priority(answer.getPriority()).
                                orderValue(answer.getOrderValue())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public QuestionWithReportsResponse saveUserAnswerAndGetNextQuestionWithReports(String authHeader, UserAnswerRequest userAnswerRequest) {
        // Извлечение информации о клиенте из токена
        CustomerInfo customerInfo = extractCustomerInfo(authHeader);

        // Получение клиента и вопроса
        Customer customer = getCustomer(customerInfo.getId());
        Question question = getQuestion(userAnswerRequest.getQuestionId());

        // Получение ответа по orderValue
        Answer answer = getAnswerByOrderValue(question, userAnswerRequest.getOrderValue());

        // Получение навыка и категории
        Skill skill = getSkill(question);
        Category category = getCategory(skill);

        // Проверка существования ответа и сохранение ответа клиента
        validateAndSaveUserAnswer(customer, question, answer, skill);

        // Обработка следующего вопроса или генерация отчета
        return processNextQuestionOrGenerateReport(question, skill, category, customer);
    }

    private CustomerInfo extractCustomerInfo(String authHeader) {
        return authService.getCustomerDetailsFromToken(authHeader);
    }

    private Customer getCustomer(Long customerId) {
        return customerFinderService.getCustomer(customerId);
    }
    private Question getQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Question not found with id: " + questionId));
    }

    private Answer getAnswerByOrderValue(Question question, Integer orderValue) {
        return question.getAnswers().stream()
                .filter(ans -> ans.getOrderValue().equals(orderValue))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Answer not found with orderValue: " + orderValue + " for question id: " + question.getId()));
    }

    private Skill getSkill(Question question) {
        return question.getSkills().stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No skill found for the question"));
    }

    private void handleUserAnswer(Customer customer, Question question, Answer answer, Skill skill) {
        checkIfAnswerExists(customer, question);
        saveUserAnswer(customer, question, answer, skill);
    }

    private void saveUserAnswer(Customer customer, Question question, Answer answer, Skill skill) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setCustomer(customer); // Установить клиента
        userAnswer.setQuestion(question); // Установить вопрос
        userAnswer.setAnswer(answer); // Установить ответ
        userAnswer.setSkill(skill); // Установить навык

        userAnswerRepository.save(userAnswer); // Попытка сохранить ответ
    }

    private void checkIfAnswerExists(Customer customer, Question question) {
        boolean exists = userAnswerRepository.existsByCustomerIdAndQuestionId(customer.getId(), question.getId());
        if (exists) {
            throw new AlreadyExistException("Customer has already answered this question");
        }
    }

//    private QuestionResponse findNextQuestion(Question currentQuestion, Skill skill) {
//        return questionService.findNextQuestion(currentQuestion.getId(), skill.getId(), currentQuestion.getJob().getId());
//    }

    private QuestionResponse buildQuestionResponse(QuestionResponse nextQuestion) {
        if (nextQuestion == null) {
            return QuestionResponse.builder()
                    .message("Test Completed!")
                    .build();
        }

        return QuestionResponse.builder()
                .id(nextQuestion.getId())
                .text(nextQuestion.getText())
                .answers(nextQuestion.getAnswers().stream()
                        .map(ans -> AnswerResponse.builder()
                                .id(ans.getId())
                                .text(ans.getText())
                                .score(ans.getScore())
                                .priority(ans.getPriority())
                                .orderValue(ans.getOrderValue())
                                .build())
                        .collect(Collectors.toList()))
                .message(null)
                .build();
    }

    private void validateAndSaveUserAnswer(Customer customer, Question question, Answer answer, Skill skill) {
        checkIfAnswerExists(customer, question);
        saveUserAnswer(customer, question, answer, skill);
    }

    private QuestionWithReportsResponse processNextQuestionOrGenerateReport(Question currentQuestion, Skill skill, Category category, Customer customer) {
        // Поиск следующего вопроса
        QuestionResponse nextQuestion = questionService.findNextQuestion(currentQuestion.getId(), skill.getId(), currentQuestion.getJob().getId(), category.getId());

        if (nextQuestion == null) {
            // Генерация отчета только для навыка последнего вопроса
            List<AggregatedReportDto> reports = reportService.generateReportForSkills(customer.getId(),category.getId());

            return QuestionWithReportsResponse.builder()
                    .reports(reports)
                    .build();
        }

        // Создание ответа для следующего вопроса
        List<AnswerResponse> answerResponses = answerService.getAnswersByQuestionId(nextQuestion.getId());

        return QuestionWithReportsResponse.builder()
                .id(nextQuestion.getId())
                .text(nextQuestion.getText())
                .answers(answerResponses)
                .build();
    }
}

