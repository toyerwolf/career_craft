package com.example.careercraft.service.impl;

import com.example.careercraft.entity.Skill;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.SkillRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SkillFinderService {


    private final SkillRepository skillRepository;

    public Skill findSkillById(Long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new NotFoundException("Skill with ID " + skillId + " not found"));
    }

}
