package com.example.careercraft.controller;

import com.example.careercraft.response.QuestionResponse;
import com.example.careercraft.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;



    @Secured("USER")
    @GetMapping("/{categoryId}/questions")
    public ResponseEntity<List<QuestionResponse>> getAllQuestionsForCategory(
            @PathVariable("categoryId") Long categoryId) {
        List<QuestionResponse> questions = categoryService.getAllQuestionsForCategory(categoryId);
        return ResponseEntity.ok(questions);
    }
}