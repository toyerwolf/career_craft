package com.example.careercraft.repository;


import com.example.careercraft.entity.Skill;
import feign.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    @NotNull List<Skill> findAllById(@NotNull Iterable<Long> ids);

    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) = LOWER(:name)")
    Optional<Skill> findByName(@Param("name") String name);

    @Query("SELECT s.id FROM Skill s")
    Collection<Long> findAllSkillIds();

    @Query("SELECT r.id FROM Report r WHERE r.skill.id IN :skillIds")
    List<Long> findReportIdsBySkillIds(List<Long> skillIds);

    List<Skill> findByCategoryId(Long categoryId);
}
