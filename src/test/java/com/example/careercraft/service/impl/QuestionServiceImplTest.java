//package com.example.careercraft.service.impl;
//
//import com.example.careercraft.entity.Job;
//import com.example.careercraft.entity.Question;
//import com.example.careercraft.exception.AlreadyExistException;
//import com.example.careercraft.repository.AnswerRepository;
//import com.example.careercraft.repository.QuestionRepository;
//import com.example.careercraft.req.AnswerRequest;
//import com.example.careercraft.req.QuestionRequest;
//import com.example.careercraft.response.QuestionResponse;
//import com.example.careercraft.service.CategoryService;
//import com.example.careercraft.service.SkillService;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentMatchers;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class QuestionServiceImplTest {
//
//    @Mock
//    private JobFinderService jobFinderService;
//
//    @Mock
//    private QuestionRepository questionRepository;
//
//    @Mock
//    private AnswerRepository answerRepository;
//
//    @Mock
//    private CategoryService categoryService;
//
//    @Mock
//    private SkillService skillService;
//
//    @InjectMocks
//    private QuestionServiceImpl questionService;
//
//   // Подготовка данных перед каждым тестом, если необходимо
//
//
//    @Test
//    @Transactional
//    void testCreateQuestion() {
//        // Given
//        Long jobId = 1L;
//        Job job = new Job(); // Создайте объект Job по необходимости
//
//
//        List<AnswerRequest> answerRequests = Arrays.asList(
//                new AnswerRequest("Answer 1", 10.0, "High", 1),
//                new AnswerRequest("Answer 2", 5.0, "Medium", 2)
//        );
//
//        // Создаем объект QuestionRequest с использованием списка AnswerRequest
//        QuestionRequest questionRequest = new QuestionRequest();
//        questionRequest.setText("Sample question");
//        questionRequest.setAnswers(answerRequests);
//        questionRequest.setSkillNames(Arrays.asList("Skill 1", "Skill 2"));
//        questionRequest.setCategoryName("Category 1");
//
//        when(jobFinderService.findJobById(jobId)).thenReturn(job);
//        when(questionRepository.save(ArgumentMatchers.any(Question.class))).thenReturn(new Question()); // Если используется репозиторий
//
//        // When
//        QuestionResponse response = questionService.createQuestion(jobId, questionRequest);
//
//        // Then
//        assertNotNull(response, "Response should not be null");
//        assertEquals("Sample question", response.getText(), "Question text should match");
//        // Добавьте дополнительные проверки в зависимости от вашей логики
//
//        verify(jobFinderService, times(1)).findJobById(jobId);
//        // Проверьте вызовы других методов, если это применимо
//    }
//}
//
//
