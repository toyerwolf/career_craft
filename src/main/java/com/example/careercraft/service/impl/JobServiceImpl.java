package com.example.careercraft.service.impl;

import com.example.careercraft.dto.JobDto;
import com.example.careercraft.entity.Job;
import com.example.careercraft.exception.AlreadyExistException;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.JobRepository;
import com.example.careercraft.service.JobService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;


    @Override
    public List<JobDto> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public JobDto getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job not found with id: " + id));
        return convertToDto(job);
    }

    public JobDto createJob(JobDto jobDto) {
        if (jobRepository.existsByName(jobDto.getName())) {
            throw new AlreadyExistException("Job with name " + jobDto.getName() + " already exists");
        }
        Job job = new Job();
        job.setName(jobDto.getName());
        Job savedJob = jobRepository.save(job);
        return convertToDto(savedJob);
    }

    @Override
    public JobDto updateJob(Long id, JobDto jobDto) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job not found with id: " + id));
        job.setName(jobDto.getName());
        Job updatedJob = jobRepository.save(job);
        return convertToDto(updatedJob);
    }

    @Override
    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new RuntimeException("Job not found with id: " + id);
        }
        jobRepository.deleteById(id);
    }

    private JobDto convertToDto(Job job) {
        JobDto jobDto = new JobDto();
//        jobDto.setId(job.getId());
        jobDto.setName(job.getName());
        return jobDto;
    }

    @Override
    public Collection<Long> getAllJobIds() {
        return jobRepository.findAllJobIds();
    }


}
