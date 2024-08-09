package com.example.careercraft.controller;

import com.example.careercraft.dto.JobDto;
import com.example.careercraft.response.QuestionResponse;
import com.example.careercraft.service.JobService;
import com.example.careercraft.service.QuestionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("jobs")
@AllArgsConstructor
public class JobController {

    private final JobService jobService;
    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<JobDto>> getAllJobs() {
        List<JobDto> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDto> getJobById(@PathVariable Long id) {
        JobDto job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }

    @PostMapping
    @Secured("ADMIN")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JobDto> createJob(@RequestBody JobDto jobDto) {
        JobDto createdJob = jobService.createJob(jobDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDto> updateJob(@PathVariable Long id, @RequestBody JobDto jobDto) {
        JobDto updatedJob = jobService.updateJob(id, jobDto);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/{id}")
    @Secured("ADMIN")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{jobId}/questions")
    @Secured("USER")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByJobId(@PathVariable Long jobId) {
        List<QuestionResponse> questionResponses = questionService.getQuestionsByJobId(jobId);
        return ResponseEntity.ok(questionResponses);
    }
}

