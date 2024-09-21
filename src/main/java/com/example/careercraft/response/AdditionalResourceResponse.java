package com.example.careercraft.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalResourceResponse {
    private List<String> books;
    private List<String> blogs;
    private List<String> tools;
    private List<String> academicPapers;
    private List<String> courses;
}