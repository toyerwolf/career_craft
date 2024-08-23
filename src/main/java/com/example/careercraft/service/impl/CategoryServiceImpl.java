package com.example.careercraft.service.impl;

import com.example.careercraft.entity.Category;
import com.example.careercraft.entity.Question;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.mapper.QuestionResponseMapper;
import com.example.careercraft.repository.CategoryRepository;
import com.example.careercraft.repository.QuestionRepository;
import com.example.careercraft.response.QuestionResponse;
import com.example.careercraft.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {



        private final CategoryRepository categoryRepository;
        private final QuestionRepository questionRepository;

        public Category findOrCreateCategoryByName(String categoryName) {
            return categoryRepository.findByName(categoryName)
                    .orElseGet(() -> createCategory(categoryName));
        }

        public Category createCategory(String categoryName) {
            Category category = new Category();
            category.setName(categoryName);
            return categoryRepository.save(category);
        }

    public Category findById(Long categoryId) {
        // Используем метод репозитория для поиска категории
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId));
    }


    public List<QuestionResponse> getAllQuestionsForCategory(Long categoryId) {
        // Получаем все вопросы для указанного categoryId
        List<Question> questions = questionRepository.findQuestionsByCategory(categoryId);
        return questions.stream()
                .map(QuestionResponseMapper::toQuestionResponse)
                .toList();
    }
    }

