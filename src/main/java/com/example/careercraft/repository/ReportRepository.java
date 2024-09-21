package com.example.careercraft.repository;

import com.example.careercraft.entity.Report;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report,Long> {
    boolean existsByCustomerIdAndSkillId(Long id, Long id1);

    Report findByCustomerIdAndSkillId(Long customerId, Long skillId);

    boolean existsByCustomerIdAndSkillIdAndCategoryId(Long customerId, Long skillId, Long categoryId);

    List<Report> findByCustomerIdAndSkillIdAndCategoryId(Long customerId, Long skillId, Long categoryId);

    Optional<Report> findByCustomerIdAndSkillIdAndValid(Long customerId, Long skillId, boolean valid);

    List<Report> findAllByCustomerIdAndCategoryIdAndValid(Long customerId, Long categoryId, boolean valid);


    @Query("SELECT r FROM Report r WHERE r.customer.id = :customerId AND r.skill.id = :skillId ORDER BY r.id DESC")
    Report findLatestReportByCustomerIdAndSkillId(@Param("customerId") Long customerId, @Param("skillId") Long skillId);
}

