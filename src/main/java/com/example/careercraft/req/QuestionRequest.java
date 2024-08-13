package com.example.careercraft.req;

import com.example.careercraft.entity.Answer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class QuestionRequest {

    @NotBlank(message = "Question text is required")
    private String text;

    @NotEmpty(message = "Answers list must not be empty")
    @Size(min = 4, max = 4, message = "There should be exactly 4 answers per question")
    private List<AnswerRequest> answers;

    private List<String> skillNames;

    private Integer orderValue;

    private String categoryName;

//    public void setSkillIds(List<Long> skillIds) {
//        if (skillIds != null) {
//            Set<Long> uniqueSkillIds = new HashSet<>(skillIds);
//            if (uniqueSkillIds.size() != skillIds.size()) {
//                throw new IllegalArgumentException("Skill IDs must be unique");
//            }
//        }
//        this.skillIds = skillIds;
//    }

}