package com.example.careercraft.controller;

import com.example.careercraft.dto.CustomerInfo;
import com.example.careercraft.dto.TestCompletionResponse;
import com.example.careercraft.req.UserAnswerRequest;

import com.example.careercraft.response.QuestionResponse;
import com.example.careercraft.response.QuestionWithReportsResponse;
import com.example.careercraft.service.AuthService;
import com.example.careercraft.service.UserAnswerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-answers")
@AllArgsConstructor
public class UserAnswerController {

    private final UserAnswerService userAnswerService;



    @Secured("USER")
    @PostMapping("/answer")
    @CrossOrigin(origins = "https://career-craft.netlify.app")
    public ResponseEntity<QuestionResponse> saveUserAnswer(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestBody UserAnswerRequest userAnswerRequest) {
        QuestionResponse response = userAnswerService.saveUserAnswerAndGetNextQuestion(authHeader, userAnswerRequest);
        return ResponseEntity.ok(response);
    }

    @Secured("USER")
    @PostMapping("/answerWithReports")
    public ResponseEntity<QuestionWithReportsResponse> saveUserAnswerWithReport(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestBody UserAnswerRequest userAnswerRequest) {
        QuestionWithReportsResponse response = userAnswerService.saveUserAnswerAndGetNextQuestionWithReports(authHeader, userAnswerRequest);
        return ResponseEntity.ok(response);
    }
}