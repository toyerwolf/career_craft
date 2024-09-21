package com.example.careercraft.repository;

import com.example.careercraft.entity.Recommendation;
import com.example.careercraft.entity.RecommendationLevel;
import com.example.careercraft.entity.Skill;
import com.example.careercraft.entity.SkillLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByLevel(RecommendationLevel level);
    List<Recommendation> findBySkill(Skill skill);
    List<Recommendation> findByLevelAndSkill(RecommendationLevel level, Skill skill);


}