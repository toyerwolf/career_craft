package com.example.careercraft.repository;

import com.example.careercraft.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report,Long> {
    boolean existsByCustomerIdAndSkillId(Long id, Long id1);

    Report findByCustomerIdAndSkillId(Long customerId, Long skillId);
}
