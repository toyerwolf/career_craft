package com.example.careercraft.service.impl;

import com.example.careercraft.entity.Category;
import com.example.careercraft.repository.CategoryRepository;
import com.example.careercraft.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {



        private CategoryRepository categoryRepository;

        public Category findOrCreateCategoryByName(String categoryName) {
            return categoryRepository.findByName(categoryName)
                    .orElseGet(() -> createCategory(categoryName));
        }

        public Category createCategory(String categoryName) {
            Category category = new Category();
            category.setName(categoryName);
            return categoryRepository.save(category);
        }
    }

