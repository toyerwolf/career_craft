package com.example.careercraft.repository;

import com.example.careercraft.entity.SkillAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillAssessmentRepository extends JpaRepository<SkillAssessment,Long> {

    Optional<SkillAssessment> findByCustomerIdAndSkillId(Long customerId, Long skillId);

    boolean existsByCustomerIdAndSkillId(Long customerId, Long skillId);

}
