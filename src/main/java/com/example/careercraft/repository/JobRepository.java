package com.example.careercraft.repository;

import com.example.careercraft.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface JobRepository extends JpaRepository<Job,Long> {
    boolean existsByName(String name);


    @Query("SELECT j.id FROM Job j")
    Collection<Long> findAllJobIds();

}
