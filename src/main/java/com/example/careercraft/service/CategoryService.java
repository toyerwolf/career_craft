package com.example.careercraft.service;

import com.example.careercraft.entity.Category;
import com.example.careercraft.response.QuestionResponse;

import java.util.List;

public interface CategoryService {

    public Category findOrCreateCategoryByName(String categoryName);

   Category createCategory(String categoryName);

    public Category findById(Long categoryId);

    public List<QuestionResponse> getAllQuestionsForCategory(Long categoryId);
}
