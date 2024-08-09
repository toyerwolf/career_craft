package com.example.careercraft.service.impl;

import com.example.careercraft.entity.Job;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.JobRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JobFinderService {

    private final JobRepository jobRepository;


    public Job findJobById(Long jobId){
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found with id " + jobId));
    }

}
