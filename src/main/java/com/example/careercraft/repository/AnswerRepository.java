package com.example.careercraft.repository;

import com.example.careercraft.entity.Answer;
import com.example.careercraft.entity.Question;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer,Long> {
    boolean existsByTextAndQuestion(String text, Question question);
    @Query("SELECT a.id FROM Answer a WHERE a.question.id = :questionId")
    List<Long> findAnswerIdsByQuestionId(@Param("questionId") Long questionId);

    List<Answer> findByQuestionId(Long questionId);
}
