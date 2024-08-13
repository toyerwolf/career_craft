package com.example.careercraft.service;

import com.example.careercraft.dto.SkillDTO;
import com.example.careercraft.entity.Category;
import com.example.careercraft.entity.Skill;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SkillService {

    Skill createSkill(String skillName, Category category);

    public Skill findOrCreateSkillByName(String skillName, String categoryName);

  void addSkillToJob(Long jobId, String skillName);

    List<SkillDTO> getAllSkills();

    public List<Long> getAllSkillIds();



}
