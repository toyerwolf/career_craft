package com.example.careercraft.repository;

import com.example.careercraft.entity.Question;
import com.example.careercraft.entity.Skill;
import feign.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {
    List<Question> findByJobId(Long jobId);

    Optional<Question> findFirstByIdGreaterThanOrderByIdAsc(Long currentQuestionId);


    @Query("SELECT q FROM Question q JOIN q.skills s WHERE s.id IN :skillIds AND q.job.id = :jobId AND q.id > :id ORDER BY q.id ASC")
    List<Question> findQuestionsBySkillsAndJob(@Param("skillIds") Collection<Long> skillIds, @Param("jobId") Long jobId, @Param("id") Long id, Pageable pageable);


    @Query("SELECT q FROM Question q " +
            "JOIN q.skills s " +
            "WHERE q.id > :currentQuestionId " +
            "AND s.id = :skillId " +
            "AND q.job.id = :jobId " +
            "ORDER BY q.id ASC")
    List<Question> findNextQuestions(
            @Param("currentQuestionId") Long currentQuestionId,
            @Param("skillId") Long skillId,
            @Param("jobId") Long jobId);

    @Query("SELECT q.id FROM Question q " +
            "JOIN q.skills s " +
            "WHERE q.id < :currentQuestionId " +
            "AND s.id = :skillId " +
            "AND q.job.id = :jobId " +
            "ORDER BY q.id DESC")
    List<Long> findPreviousQuestionIdsBySkillAndJob(
            @Param("skillId") Long skillId,
            @Param("jobId") Long jobId,
            @Param("currentQuestionId") Long currentQuestionId,
            Pageable pageable);


    @Query("SELECT q FROM Question q " +
            "JOIN q.skills s " +
            "WHERE s.category.id = :categoryId " +
            "AND s.id IN :skillIds " +
            "AND q.job.id = :jobId " +
            "AND q.id = :id " +
            "ORDER BY q.id ASC")
    List<Question> findFirstQuestionByCategoryAndSkills(
            @Param("categoryId") Long categoryId,
            @Param("skillIds") Collection<Long> skillIds,
            @Param("jobId") Long jobId,
            @Param("id") Long id,
            Pageable pageable);

    Optional<Question> findByText(String text);

    boolean existsByText(String text);

    @Query("SELECT COUNT(q) FROM Question q JOIN q.skills s WHERE s.id = :skillId")
    long countBySkillId(@Param("skillId") Long skillId);

    @Query("SELECT q FROM Question q " +
            "JOIN q.skills s " +
            "WHERE s.id = :skillId " +
            "AND q.job.id = :jobId " +
            "ORDER BY q.id ASC")
    List<Question> findQuestionsForSkill(
            @Param("skillId") Long skillId,
            @Param("jobId") Long jobId);

    @Query("SELECT s FROM Skill s " +
            "WHERE s.id > :currentSkillId " +
            "ORDER BY s.id ASC")
    List<Skill> findNextSkills(
            @Param("currentSkillId") Long currentSkillId);

    @Query("SELECT q FROM Question q JOIN q.skills s WHERE s.id = :skillId")
    List<Question> findBySkillId(@Param("skillId") Long skillId);

    List<Question> findBySkillsIdAndJobId(Long skillId, Long jobId);

    @Query("SELECT DISTINCT q.skills FROM Question q WHERE q.job.id = :jobId")
    List<Skill> findAllSkills(@Param("jobId") Long jobId);



    @Query("SELECT q.id FROM Question q")
    List<Long> findAllQuestionIds();

    @Query("SELECT q FROM Question q " +
            "JOIN q.skills s " +
            "WHERE s.id = :skillId " +
            "AND q.job.id = :jobId " +
            "AND s.category.id = :categoryId " +
            "ORDER BY q.id")
    List<Question> findQuestionsBySkillIdAndJobIdAndCategoryId(
            @Param("skillId") Long skillId,
            @Param("jobId") Long jobId,
            @Param("categoryId") Long categoryId);

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.skills s LEFT JOIN FETCH s.category WHERE q.skills IS NOT EMPTY AND s.category.id = :categoryId")
    List<Question> findQuestionsByCategory(@Param("categoryId") Long categoryId);
}

