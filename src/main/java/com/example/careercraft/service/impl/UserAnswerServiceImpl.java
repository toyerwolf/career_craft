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
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;


    @Transactional
    @Override
    public QuestionResponse saveUserAnswerAndGetNextQuestion(String authHeader, UserAnswerRequest userAnswerRequest) {
        // Извлечение информации о клиенте из заголовка авторизации (токена)
        CustomerInfo customerInfo = extractCustomerInfo(authHeader);

        // Получение клиента из базы данных по идентификатору, извлеченному из токена
        Customer customer = getCustomer(customerInfo.getId());

        // Получение вопроса из базы данных по идентификатору, предоставленному в запросе
        Question question = getQuestion(userAnswerRequest.getQuestionId());

        // Извлечение ответа пользователя из запроса по значению порядка (если это некое перечисление)
        Answer answer = getAnswerByOrderValue(question, userAnswerRequest.getOrderValue());

        // Получение навыка, к которому относится вопрос
        Skill skill = getSkill(question);

        // Получение категории, к которой относится навык
        Category category = getCategory(skill);

        // Обработка ответа пользователя (сохранение в базе данных, обновление статистики и т.д.)
        handleUserAnswer(customer, question, answer, skill);

        // Определение следующего вопроса для пользователя в зависимости от состояния вопросов и навыков
        return getNextQuestionResponse(customer, skill, category);
    }

    private QuestionResponse getNextQuestionResponse(Customer customer, Skill currentSkill, Category currentCategory) {
        // Проверяем, остались ли вопросы в текущем навыке
        if (areQuestionsRemainingInSkill(customer, currentSkill)) {
            // Если вопросы остались, находим следующий вопрос в текущем навыке
            return findNextQuestionInSkill(customer, currentSkill);
        }

        // Если вопросы в текущем навыке закончились, ищем следующий навык в текущей категории
        Optional<Skill> nextSkill = findNextSkillInCategory(customer, currentCategory);
        if (nextSkill.isPresent()) {
            // Если найден следующий навык, находим следующий вопрос в этом навыке
            return findNextQuestionInSkill(customer, nextSkill.get());
        }

        // Если не найден следующий навык, ищем следующую категорию
        Optional<Category> nextCategory = findNextCategory(customer, currentCategory);
        if (nextCategory.isPresent()) {
            // Если найдена следующая категория, находим следующий вопрос в этой категории
            return findNextQuestionInCategory(customer, nextCategory.get());
        }

        // Если не осталось вопросов ни в одной категории, возвращаем ответ о завершении теста
        return getTestCompletionResponse();
    }

    private boolean areQuestionsRemainingInSkill(Customer customer, Skill skill) {
        // Получаем все вопросы для текущего навыка
        List<Question> questionsForSkill = questionRepository.findBySkillId(skill.getId());

        // Проверяем, есть ли среди вопросов те, на которые пользователь еще не ответил
        return questionsForSkill.stream()
                .anyMatch(q -> !userAnswerRepository.existsByCustomerIdAndQuestionId(customer.getId(), q.getId()));
    }

    private QuestionResponse findNextQuestionInSkill(Customer customer, Skill skill) {
        List<Question> questionsForSkill = questionRepository.findBySkillId(skill.getId());
        Optional<Question> nextQuestion = questionsForSkill.stream()
                .filter(q -> !userAnswerRepository.existsByCustomerIdAndQuestionId(customer.getId(), q.getId()))
                .findFirst();

        return nextQuestion.map(q -> buildQuestionResponse(q, skill, customer))
                .orElseGet(this::getTestCompletionResponse);
    }

    private Optional<Skill> findNextSkillInCategory(Customer customer, Category category) {
        // Получаем все навыки в текущей категории
        List<Skill> skillsInCategory = skillRepository.findByCategoryId(category.getId());
        // Находим первый навык в категории, для которого остались вопросы
        return skillsInCategory.stream()
                .filter(skill -> areQuestionsRemainingInSkill(customer, skill))
                .findFirst();
    }

    private Optional<Category> findNextCategory(Customer customer, Category currentCategory) {
        // Получаем все категории
        List<Category> allCategories = categoryRepository.findAll();
        // Находим первую категорию, которая не является текущей и для которой остались навыки с вопросами
        return allCategories.stream()
                .filter(category -> !category.equals(currentCategory) && areSkillsRemainingInCategory(customer, category))
                .findFirst();
    }

    private boolean areSkillsRemainingInCategory(Customer customer, Category category) {
        // Получаем все навыки в категории
        List<Skill> skillsInCategory = skillRepository.findByCategoryId(category.getId());
        // Проверяем, есть ли среди навыков те, для которых остались вопросы
        return skillsInCategory.stream()
                .anyMatch(skill -> areQuestionsRemainingInSkill(customer, skill));
    }

    private QuestionResponse findNextQuestionInCategory(Customer customer, Category category) {
        // Получаем все навыки в текущей категории
        List<Skill> skillsInCategory = skillRepository.findByCategoryId(category.getId());
        // Находим первый навык, для которого остались вопросы
        Optional<Skill> nextSkill = skillsInCategory.stream()
                .filter(skill -> areQuestionsRemainingInSkill(customer, skill))
                .findFirst();

        // Если найден следующий навык, находим и возвращаем следующий вопрос для этого навыка
        return nextSkill.map(skill -> findNextQuestionInSkill(customer, skill))
                .orElseGet(this::getTestCompletionResponse);
    }

    private QuestionResponse buildQuestionResponse(Question question, Skill skill, Customer customer) {
        return QuestionResponse.builder()
                .header("Skill: " + skill.getName()) // Заголовок с названием навыка
                .id(question.getId())
                .text(question.getText()) // Текст вопроса
                .answers(question.getAnswers().stream()
                        .map(answer -> AnswerResponse.builder()
                                .id(answer.getId())
                                .text(answer.getText()) // Текст ответа
                                .score(answer.getScore()) // Оценка ответа
                                .priority(answer.getPriority()) // Приоритет ответа
                                .orderValue(answer.getOrderValue()) // Значение порядка ответа
                                .build())
                        .collect(Collectors.toList())) // Список ответов
                .answeredQuestionsCount(customer.getAnsweredQuestionsCount()) // Установка количества отвеченных вопросов
                .build();
    }

    private QuestionResponse getTestCompletionResponse() {
        // Возвращаем сообщение о завершении теста
        return QuestionResponse.builder()
                .message("Test Completed for Category!")
                .build();
    }





    private Category getCategory(Skill skill) {
        return Optional.ofNullable(skill.getCategory())
                .orElseThrow(() -> new NotFoundException("No category found for the skill"));
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
        incrementAnsweredQuestionsCount(customer);
        saveUserAnswer(customer, question, answer, skill);
    }

    private void incrementAnsweredQuestionsCount(Customer customer) {
        // Увеличиваем счетчик отвеченных вопросов
        customer.setAnsweredQuestionsCount(customer.getAnsweredQuestionsCount() + 1);

        // Сохраняем изменения в базе данных
        customerRepository.save(customer);
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

