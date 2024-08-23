package com.example.careercraft.controller;


import com.example.careercraft.dto.QuestionIdsDto;
import com.example.careercraft.dto.SkillQuestionResponse;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.req.QuestionRequest;

import com.example.careercraft.response.DetailedSkillQuestionResponse;
import com.example.careercraft.response.QuestionResponse;
import com.example.careercraft.service.JobService;
import com.example.careercraft.service.QuestionService;
import com.example.careercraft.service.SkillService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/questions")
@AllArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final SkillService skillService;
    private final JobService jobService;


    @GetMapping
    public List<QuestionIdsDto> getAllQuestions() {
        return questionService.getAllQuestions();
    }


    @Secured("ADMIN")
    @PostMapping("/{jobId}")
    public ResponseEntity<QuestionResponse> createQuestion(@PathVariable Long jobId, @RequestBody QuestionRequest questionRequest) {
        QuestionResponse createdQuestion = questionService.createQuestion(jobId, questionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
    }

    @Secured("USER")
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable Long questionId){
        QuestionResponse questionResponse=questionService.getQuestionById(questionId);
        return ResponseEntity.ok(questionResponse);
    }


    @PutMapping("/jobs/{jobId}/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable("jobId") Long jobId,
            @PathVariable("questionId") Long questionId,
            @RequestBody QuestionRequest questionRequest) {

        QuestionResponse updatedQuestion = questionService.updateQuestion(jobId, questionId, questionRequest);
        return ResponseEntity.ok(updatedQuestion);
    }
//
//    @GetMapping("/firstQuestionFromSkill")
//    @Secured("USER")
//    public ResponseEntity<SkillQuestionResponse> getFirstQuestionFromSkill(
//            @RequestParam("skillIds") Collection<Long> skillIds,
//            @RequestParam("jobId") Long jobId, // Добавлен параметр jobId
//            @RequestParam(value = "id", defaultValue = "0") Long id) {
//        SkillQuestionResponse skillQuestionResponse = questionService.findQuestionsGroupedBySkills(skillIds, jobId, id);
//        return ResponseEntity.ok(skillQuestionResponse);
//    }


    @Secured("USER")
    @GetMapping("/nextQuestionInSkill")
    public ResponseEntity<QuestionResponse> getNextQuestion(
            @RequestParam("currentQuestionId") Long currentQuestionId,
            @RequestParam("skillId") Long skillId,
            @RequestParam("jobId") Long jobId,
            @RequestParam("categoryId") Long categoryId)
    { // Добавлен параметр jobId
        // Вызов метода сервиса, который может выбросить исключение
        QuestionResponse questionResponse = questionService.findNextQuestion(currentQuestionId, skillId, jobId,categoryId);
        return ResponseEntity.ok(questionResponse);
    }
//
//    @GetMapping("/previousQuestionInSkill")
//    @Secured("USER")
//    public ResponseEntity<QuestionResponse> getPreviousQuestion(
//            @RequestParam("skillId") Long skillId,
//            @RequestParam("currentQuestionId") Long currentQuestionId,
//            @RequestParam("jobId") Long jobId) { // Добавлен параметр jobId
//        // Вызов метода сервиса, который может выбросить исключение
//        QuestionResponse questionResponse = questionService.getPreviousQuestion(skillId, jobId, currentQuestionId);
//        return ResponseEntity.ok(questionResponse);
//    }

//    @GetMapping("/getInitialData")
//    @Secured("USER")
//    public ResponseEntity<Map<String, Object>> getInitialData() {
//        Map<String, Object> response = new HashMap<>();
//
//        // Получаем идентификаторы всех навыков
//        Collection<Long> skillIds = skillService.getAllSkillIds();
//
//        // Получаем идентификаторы всех работ
//        Collection<Long> jobIds = jobService.getAllJobIds();
//
//        // Можно добавить id, если он нужен, например, дефолтный ID
//        // Long id = jobService.getDefaultId(); // Если этот метод существует и нужен
//
//        response.put("skillIds", skillIds);
//        response.put("jobIds", jobIds);
//        // response.put("id", id); // Если id не нужен, можно удалить эту строку
//
//        return ResponseEntity.ok(response);
//    }



    @Secured("USER")
    @GetMapping("/firstQuestionsFromCategory")
    public ResponseEntity<DetailedSkillQuestionResponse> getFirstQuestionsFromCategory(
            @RequestParam("skillIds") Collection<Long> skillIds,
            @RequestParam("jobId") Long jobId,
            @RequestParam("categoryId") Long categoryId) {
        try {
            DetailedSkillQuestionResponse skillQuestionResponse = questionService.findFirstQuestionsFromCategoryAndSkills(skillIds, jobId, categoryId);
            return ResponseEntity.ok(skillQuestionResponse);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



    @Secured("USER")
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalQuestionsCount() {
        long count = questionService.getTotalQuestionsCount();
        return ResponseEntity.ok(count);
    }



}
