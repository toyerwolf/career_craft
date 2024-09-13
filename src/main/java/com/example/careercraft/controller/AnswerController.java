package com.example.careercraft.controller;

import com.example.careercraft.response.AnswerResponse;
import com.example.careercraft.service.AnswerService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/answers")
@AllArgsConstructor
public class AnswerController {

    private final AnswerService answerService;




    @Secured("USER")
    @GetMapping("/question/{questionId}")
    @CrossOrigin(origins = "https://career-craft.netlify.app")
    public List<AnswerResponse> getAnswersByQuestionId(@PathVariable Long questionId) {
        return answerService.getAnswersByQuestionId(questionId);
    }
}