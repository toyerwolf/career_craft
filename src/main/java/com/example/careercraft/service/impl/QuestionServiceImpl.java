package com.example.careercraft.service.impl;


import com.example.careercraft.dto.QuestionIdsDto;
import com.example.careercraft.dto.SkillQuestionResponse;
import com.example.careercraft.entity.Answer;
import com.example.careercraft.entity.Job;
import com.example.careercraft.entity.Question;
import com.example.careercraft.entity.Skill;
import com.example.careercraft.exception.AlreadyExistException;
import com.example.careercraft.exception.NotFoundException;

import com.example.careercraft.mapper.QuestionResponseMapper;
import com.example.careercraft.mapper.SkillQuestionResponseMapper;
import com.example.careercraft.repository.JobRepository;
import com.example.careercraft.repository.QuestionRepository;
import com.example.careercraft.repository.SkillRepository;
import com.example.careercraft.req.AnswerRequest;
import com.example.careercraft.req.QuestionRequest;

import com.example.careercraft.response.AnswerResponse;
import com.example.careercraft.response.QuestionResponse;

import com.example.careercraft.service.AnswerService;
import com.example.careercraft.service.QuestionService;
import com.example.careercraft.service.SkillService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerService answerService;
    private final SkillRepository skillRepository;
    private final JobFinderService jobFinderService;
    private final SkillService skillService;
    private final JobRepository jobRepository;






    @Transactional
    @Override
    public QuestionResponse createQuestion(Long jobId, QuestionRequest questionRequest) {
        Job job = jobFinderService.findJobById(jobId);
        checkQuestionExists(questionRequest.getText());
        Question question = createQuestion(questionRequest, job);
        createAndSetAnswers(question, questionRequest.getAnswers());
        processSkills(question, questionRequest.getSkillNames(),job);
        return QuestionResponseMapper.toQuestionResponse(question);
    }

    private void checkQuestionExists(String questionText) {
        if (questionRepository.existsByText(questionText)) {
            throw new AlreadyExistException("Question with this text already exists");
        }
    }

    private Question createQuestion(QuestionRequest questionRequest, Job job) {
        Question question = new Question();
        question.setText(questionRequest.getText());
        question.setJob(job);
        return questionRepository.save(question);
    }

    private void createAndSetAnswers(Question question, List<AnswerRequest> answerRequests) {
        List<Answer> answers = answerService.createAnswersForQuestion(question, answerRequests);
        question.setAnswers(new HashSet<>(answers));
        questionRepository.save(question); // Обновляем вопрос с ответами
    }

    private void processSkills(Question question, List<String> skillNames, Job job) {
        if (skillNames != null && !skillNames.isEmpty()) {
            Set<Skill> skillsToAdd = new HashSet<>();

            // Находим или создаем навыки
            for (String skillName : skillNames) {
                Skill skill = skillService.findOrCreateSkillByName(skillName);
                skillsToAdd.add(skill);
            }

            // Получаем текущие навыки вопроса
            Set<Skill> existingSkills = question.getSkills();

            // Обновляем навыки вопроса, добавляя новые, которые ещё не связаны с вопросом
            for (Skill skill : skillsToAdd) {
                if (!existingSkills.contains(skill)) {
                    existingSkills.add(skill);
                    skill.getQuestions().add(question); // Добавляем вопрос к навыку
                }
            }

            // Сохраняем изменения
            questionRepository.save(question);  // Обновляем вопрос с навыками
            skillRepository.saveAll(skillsToAdd); // Сохраняем изменения в навыках

            // Добавляем навыки к работе
            for (Skill skill : skillsToAdd) {
               skillService.addSkillToJob(job.getId(), skill.getName());
            }
        }
    }

    @Override
    public QuestionResponse getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Question not found with id " + questionId));
        return QuestionResponseMapper.toQuestionResponse(question);
    }

    @Override
    @Transactional()
    public List<QuestionResponse> getQuestionsByJobId(Long jobId) {
        List<Question> questions = questionRepository.findByJobId(jobId);
        return questions.stream()
                .map(QuestionResponseMapper::toQuestionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionResponse getNextQuestion(Long currentQuestionId) {
        Optional<Question> nextQuestion = questionRepository.findFirstByIdGreaterThanOrderByIdAsc(currentQuestionId);
        return nextQuestion.map(QuestionResponseMapper::toQuestionResponse).orElse(null);
    }


    @Transactional
    public SkillQuestionResponse findQuestionsGroupedBySkills(Collection<Long> skillIds, Long jobId, Long id) {
        Pageable pageable = PageRequest.of(0, 1); // Запрашиваем только первый элемент
        List<Question> questions = questionRepository.findQuestionsBySkillsAndJob(skillIds, jobId, id, pageable);
        if (questions.isEmpty()) {
            throw new NotFoundException("No question found for the given skill IDs and ID.");
        }
        return SkillQuestionResponseMapper.toSkillQuestionResponse(Optional.ofNullable(questions.get(0)));
    }

    @Transactional
    @Override
    public QuestionResponse findNextQuestion(Long currentQuestionId, Long skillId, Long jobId) {
        // Найти следующий вопрос по текущему вопросу, skillId и jobId
        Question nextQuestion = findNextQuestionInCurrentSkill(currentQuestionId, skillId, jobId);
        if (nextQuestion != null) {
            return QuestionResponseMapper.toQuestionResponse(nextQuestion);
        }
        return null;
    }

    private Question findNextQuestionInCurrentSkill(Long currentQuestionId, Long skillId, Long jobId) {
        List<Question> questions = questionRepository.findQuestionsForSkill(skillId, jobId);
        // Найти следующий вопрос в списке
        return questions.stream()
                .filter(q -> q.getId() > currentQuestionId)
                .findFirst()
                .orElse(null);
    }
    @Transactional
    public QuestionResponse getPreviousQuestion(Long skillId, Long jobId, Long currentQuestionId) {
        // Найти идентификатор предыдущего вопроса по skillId, jobId и текущему вопросу
        List<Long> previousQuestionIds = questionRepository.findPreviousQuestionIdsBySkillAndJob(skillId, jobId, currentQuestionId, PageRequest.of(0, 1));

//        if (previousQuestionIds.isEmpty()) {
//            throw new NotFoundException("Previous question not found for skill ID: " + skillId + " and job ID: " + jobId);
//        }

        Long previousQuestionId = previousQuestionIds.get(0);
        Question question = questionRepository.findById(previousQuestionId)
                .orElseThrow(() -> new NotFoundException("Previous question not found"));
        return QuestionResponseMapper.toQuestionResponse(question);
    }

    public List<QuestionIdsDto> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(question -> {
                    // Получаем список ответов для текущего вопроса в формате DTO
                    List<AnswerResponse> answers = answerService.getAnswersByQuestionId(question.getId());
                    return new QuestionIdsDto(question.getId(), question.getText(), answers);
                })
                .collect(Collectors.toList());
    }
    }


