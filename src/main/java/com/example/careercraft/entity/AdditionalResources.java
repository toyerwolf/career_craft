package com.example.careercraft.entity;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalResources {

    @ElementCollection
    @CollectionTable(name = "additional_resources_books", joinColumns = @JoinColumn(name = "recommendation_id"))
    private List<String> books;

    @ElementCollection
    @CollectionTable(name = "additional_resources_blogs", joinColumns = @JoinColumn(name = "recommendation_id"))
    private List<String> blogs;

    @ElementCollection
    @CollectionTable(name = "additional_resources_tools", joinColumns = @JoinColumn(name = "recommendation_id"))
    private List<String> tools;

    @ElementCollection
    @CollectionTable(name = "additional_resources_academic_papers", joinColumns = @JoinColumn(name = "recommendation_id"))
    private List<String> academicPapers;

    @ElementCollection
    @CollectionTable(name = "additional_resources_courses", joinColumns = @JoinColumn(name = "recommendation_id"))
    private List<String> courses;
}