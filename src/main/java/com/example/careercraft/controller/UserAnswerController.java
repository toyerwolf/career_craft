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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-answers")
@AllArgsConstructor
public class UserAnswerController {

    private final UserAnswerService userAnswerService;


    @PostMapping("/answer")
    public ResponseEntity<QuestionResponse> saveUserAnswer(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestBody UserAnswerRequest userAnswerRequest) {
        QuestionResponse response = userAnswerService.saveUserAnswerAndGetNextQuestion(authHeader, userAnswerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/answerWithReports")
    public ResponseEntity<QuestionWithReportsResponse> saveUserAnswerWithReport(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestBody UserAnswerRequest userAnswerRequest) {
        QuestionWithReportsResponse response = userAnswerService.saveUserAnswerAndGetNextQuestionWithReports(authHeader, userAnswerRequest);
        return ResponseEntity.ok(response);
    }
}