package com.example.careercraft.service;

import com.example.careercraft.entity.Category;

public interface CategoryService {

    public Category findOrCreateCategoryByName(String categoryName);

   Category createCategory(String categoryName);

    public Category findById(Long categoryId);
}
