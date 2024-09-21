package com.example.careercraft.service.impl;

import com.example.careercraft.entity.*;
import com.example.careercraft.exception.AlreadyExistException;
import com.example.careercraft.repository.AnswerRepository;
import com.example.careercraft.repository.QuestionRepository;
import com.example.careercraft.repository.SkillRepository;
import com.example.careercraft.req.AnswerRequest;
import com.example.careercraft.req.QuestionRequest;
import com.example.careercraft.response.QuestionResponse;
import com.example.careercraft.service.AnswerService;
import com.example.careercraft.service.CategoryService;
import com.example.careercraft.service.SkillService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

    @Mock
    private JobFinderService jobFinderService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Mock
    private AnswerService answerService;

    @Mock
    private SkillRepository skillRepository;

   // Подготовка данных перед каждым тестом, если необходимо



    @Test
    @Transactional
    void testCreateQuestion() {
        // Given
        Long jobId = 1L;
        Job job = new Job(); // Создайте объект Job по необходимости

        List<AnswerRequest> answerRequests = Arrays.asList(
                new AnswerRequest("Answer 1", 10.0, "High", 1),
                new AnswerRequest("Answer 2", 5.0, "Medium", 2)
        );

        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setText("Sample question");
        questionRequest.setAnswers(answerRequests);
        questionRequest.setSkillNames(Arrays.asList("Skill 1", "Skill 2"));
        questionRequest.setCategoryName("Category 1");

        Category category = new Category();
        category.setName("Category 1");

        Skill skill1 = new Skill();
        skill1.setName("Skill 1");
        skill1.setCategory(category);

        Skill skill2 = new Skill();
        skill2.setName("Skill 2");
        skill2.setCategory(category);

        Question question = new Question();
        question.setText("Sample question");
        question.setJob(job);

        // Создание ответов с помощью сеттеров
        Answer answer1 = new Answer();
        answer1.setId(1L);
        answer1.setText("Answer 1");
        answer1.setScore(10.0);
        answer1.setOrderValue(1);
        answer1.setPriority("High");
        answer1.setQuestion(question);

        Answer answer2 = new Answer();
        answer2.setId(2L);
        answer2.setText("Answer 2");
        answer2.setScore(5.0);
        answer2.setOrderValue(2);
        answer2.setPriority("Medium");
        answer2.setQuestion(question);

        when(jobFinderService.findJobById(jobId)).thenReturn(job);
        when(questionRepository.save(ArgumentMatchers.any(Question.class))).thenReturn(question);
        when(answerService.createAnswersForQuestion(ArgumentMatchers.any(Question.class), ArgumentMatchers.anyList()))
                .thenReturn(Arrays.asList(answer1, answer2));
        when(categoryService.findOrCreateCategoryByName("Category 1")).thenReturn(category);
        when(skillService.findOrCreateSkillByName("Skill 1", "Category 1")).thenReturn(skill1);
        when(skillService.findOrCreateSkillByName("Skill 2", "Category 1")).thenReturn(skill2);

        // When
        QuestionResponse response = questionService.createQuestion(jobId, questionRequest);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals("Sample question", response.getText(), "Question text should match");
        assertEquals(2, response.getAnswers().size(), "Should have two answers");
        assertTrue(response.getAnswers().stream().anyMatch(a -> "Answer 1".equals(a.getText())), "Should contain 'Answer 1'");
        assertTrue(response.getAnswers().stream().anyMatch(a -> "Answer 2".equals(a.getText())), "Should contain 'Answer 2'");

        verify(jobFinderService, times(1)).findJobById(jobId);
        verify(questionRepository, times(3)).save(ArgumentMatchers.any(Question.class));
        verify(answerService, times(1)).createAnswersForQuestion(any(Question.class), anyList());
        verify(categoryService, times(1)).findOrCreateCategoryByName("Category 1");
        verify(skillService, times(2)).findOrCreateSkillByName(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }
}


