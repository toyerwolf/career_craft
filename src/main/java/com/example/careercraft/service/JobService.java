package com.example.careercraft.service;

import com.example.careercraft.dto.JobDto;
import com.example.careercraft.entity.Job;

import java.util.Collection;
import java.util.List;

public interface JobService {

    List<JobDto> getAllJobs();
    JobDto getJobById(Long id);
    JobDto createJob(JobDto jobDto);
    JobDto updateJob(Long id, JobDto jobDto);
    void deleteJob(Long id);
    Collection<Long> getAllJobIds();
}
