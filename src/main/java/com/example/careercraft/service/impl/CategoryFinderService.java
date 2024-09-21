package com.example.careercraft.service.impl;

import com.example.careercraft.entity.Category;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryFinderService {

    private final CategoryRepository categoryRepository;


    public Category findCategoryById(Long categoryId){
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));

    }
}
