package com.example.careercraft.service.impl;

import com.example.careercraft.dto.SkillDTO;
import com.example.careercraft.entity.Job;
import com.example.careercraft.entity.Skill;
import com.example.careercraft.repository.JobRepository;
import com.example.careercraft.repository.SkillRepository;
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


    @Transactional
    public Skill findOrCreateSkillByName(String name) {
        return skillRepository.findByName(name)
                .orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setName(name);
                    return skillRepository.save(newSkill);
                });
    }

    @Transactional
    public void addSkillToJob(Long jobId, String skillName) {
        Job job = jobFinderService.findJobById(jobId);
        Skill skill = skillRepository.findByName(skillName)
                .orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setName(skillName);
                    return skillRepository.save(newSkill);
                });
        job.addSkill(skill);
        jobRepository.save(job); // Сохраняем изменения в Job
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