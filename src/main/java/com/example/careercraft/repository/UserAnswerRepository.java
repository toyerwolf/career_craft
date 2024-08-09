package com.example.careercraft.repository;

import com.example.careercraft.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer,Long> {
    List<UserAnswer> findByCustomerId(Long userId);

    boolean existsByCustomerIdAndQuestionId(Long id, Long id1);
}
