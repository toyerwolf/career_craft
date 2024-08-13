package com.example.careercraft.repository;

import com.example.careercraft.entity.AggregatedReport;
import feign.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AggregatedReportRepository extends JpaRepository<AggregatedReport, Long> {
    List<AggregatedReport> findByCustomerIdAndCategoryId(Long customerId, Long categoryId);

    Optional<AggregatedReport> findByCustomerIdAndCategoryIdAndValid(Long customerId, Long categoryId, boolean valid);

    // Удаление отчета
    void delete(@NotNull AggregatedReport report);
}