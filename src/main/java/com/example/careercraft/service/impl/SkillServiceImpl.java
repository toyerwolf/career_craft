package com.example.careercraft.service.impl;

import com.example.careercraft.dto.SkillDTO;
import com.example.careercraft.entity.Category;
import com.example.careercraft.entity.Job;
import com.example.careercraft.entity.Skill;
import com.example.careercraft.repository.JobRepository;
import com.example.careercraft.repository.SkillRepository;
import com.example.careercraft.service.CategoryService;
import com.example.careercraft.service.SkillService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SkillServiceImpl  implements SkillService {

    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final JobFinderService jobFinderService;
    private final CategoryService categoryService;


    public Skill findOrCreateSkillByName(String skillName, String categoryName) {
        // Приведение строк к нижнему регистру
        String lowerCaseSkillName = skillName.trim().toLowerCase();
        String lowerCaseCategoryName = categoryName.trim().toLowerCase();

        Category category = categoryService.findOrCreateCategoryByName(lowerCaseCategoryName);
        return skillRepository.findByName(lowerCaseSkillName)
                .map(skill -> {
                    // Обновляем категорию, если она была изменена
                    if (!skill.getCategory().getName().equals(lowerCaseCategoryName)) {
                        skill.setCategory(category);
                        return skillRepository.save(skill); // Сохраняем изменения
                    }
                    return skill;
                })
                .orElseGet(() -> createSkill(lowerCaseSkillName, category));
    }

    public Skill createSkill(String skillName, Category category) {
        String lowerCaseSkillName = skillName.trim().toLowerCase(); // Приведение к нижнему регистру
        Skill skill = new Skill();
        skill.setName(lowerCaseSkillName);
        skill.setCategory(category);
        return skillRepository.save(skill);
    }
    @Transactional
    public void addSkillToJob(Long jobId, String skillName) {
        // Приведение строки к нижнему регистру
        String lowerCaseSkillName = skillName.trim().toLowerCase();

        Job job = jobFinderService.findJobById(jobId);
        Skill skill = skillRepository.findByName(lowerCaseSkillName)
                .orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setName(lowerCaseSkillName);
                    return skillRepository.save(newSkill);
                });
        if (!job.getSkills().contains(skill)) {
            job.addSkill(skill);
            jobRepository.save(job); // Сохраняем изменения в Job
        }
    }

    public List<SkillDTO> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(skill -> new SkillDTO(skill.getId(), skill.getName()))
                .collect(Collectors.toList());
    }

    public List<Long> getAllSkillIds() {
        return skillRepository.findAll().stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}