package com.example.careercraft.service.impl;

import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.response.AdditionalResourceResponse;
import com.example.careercraft.response.RecommendationResponse;
import com.example.careercraft.entity.*;
import com.example.careercraft.repository.RecommendationRepository;
import com.example.careercraft.repository.ReportRepository;
import com.example.careercraft.req.RecommendationRequest;
import com.example.careercraft.service.ImageService;
import com.example.careercraft.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final ReportRepository reportRepository;
    private final SkillFinderService skillFinderService;
    private final CategoryFinderService categoryFinderService;
    private final ImageService imageService;

    @Value("${image-url-prefix}")
    private String imageUrlPrefix;

    @Override
    public RecommendationResponse addRecommendation(RecommendationRequest recommendationRequest, MultipartFile image) {
        return saveOrUpdateRecommendation(Optional.empty(), recommendationRequest, image);
    }

    @Override
    public RecommendationResponse updateRecommendation(Long recommendationId, RecommendationRequest recommendationRequest, MultipartFile image) {
        return saveOrUpdateRecommendation(Optional.of(recommendationId), recommendationRequest, image);
    }

    private RecommendationResponse saveOrUpdateRecommendation(Optional<Long> recommendationIdOpt, RecommendationRequest recommendationRequest, MultipartFile image) {
        Recommendation recommendation = recommendationIdOpt.map(this::findRecommendationById)
                .orElse(new Recommendation());

        Skill skill = skillFinderService.findSkillById(recommendationRequest.getSkillId());
        Category category = categoryFinderService.findCategoryById(recommendationRequest.getCategoryId());
        String imagePath = image != null ? imageService.saveImage(image) : recommendation.getImagePath();

        populateRecommendationFields(recommendation, recommendationRequest, skill, category, imagePath);

        Recommendation savedRecommendation = recommendationRepository.save(recommendation);
        return toResponse(savedRecommendation);
    }

    private Recommendation findRecommendationById(Long recommendationId) {
        return recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new NotFoundException("Recommendation not found"));
    }

    private void populateRecommendationFields(Recommendation recommendation, RecommendationRequest request, Skill skill, Category category, String imagePath) {
        recommendation.setSkill(skill);
        recommendation.setCategory(category);
        recommendation.setDescription(request.getDescription());
        recommendation.setLevel(request.getLevel());
        recommendation.setAction(request.getAction());
        AdditionalResources additionalResources = getAdditionalResources(request);

        recommendation.setAdditionalResources(additionalResources);
        recommendation.setImagePath(imagePath);
    }

    @NotNull
    private static AdditionalResources getAdditionalResources(RecommendationRequest request) {
        AdditionalResources additionalResources = new AdditionalResources();
        additionalResources.setBooks(request.getAdditionalResources().getBooks());
        additionalResources.setBlogs(request.getAdditionalResources().getBlogs());
        additionalResources.setTools(request.getAdditionalResources().getTools());
        additionalResources.setAcademicPapers(request.getAdditionalResources().getAcademicPapers());
        additionalResources.setCourses(request.getAdditionalResources().getCourses());
        return additionalResources;
    }

    private RecommendationResponse toResponse(Recommendation recommendation) {
        RecommendationResponse response = new RecommendationResponse();
        response.setCategoryId(recommendation.getCategory().getId());
        response.setCategoryName(recommendation.getCategory().getName());
        response.setDescription(recommendation.getDescription());
        response.setSkillId(recommendation.getSkill().getId());
        response.setSkillName(recommendation.getSkill().getName());
        response.setLevel(recommendation.getLevel());
        response.setAction(recommendation.getAction());

        if (recommendation.getImagePath() != null) {
            response.setImageUrl(imageUrlPrefix + recommendation.getImagePath());
        }

        if (recommendation.getAdditionalResources() != null) {
            response.setAdditionalResources(toAdditionalResourceResponse(recommendation.getAdditionalResources()));
        }

        return response;
    }

    private AdditionalResourceResponse toAdditionalResourceResponse(AdditionalResources additionalResources) {
        AdditionalResourceResponse response = new AdditionalResourceResponse();
        response.setBooks(additionalResources.getBooks());
        response.setBlogs(additionalResources.getBlogs());
        response.setTools(additionalResources.getTools());
        response.setAcademicPapers(additionalResources.getAcademicPapers());
        response.setCourses(additionalResources.getCourses());

        return response;
    }

    public List<RecommendationResponse> getRecommendationsForCustomer(Long customerId, Long skillId) {
        Skill skill = skillFinderService.findSkillById(skillId);
        if (skill == null) {
            throw new NotFoundException("Skill not found with id: " + skillId);
        }

        SkillLevel skillLevel = getSkillLevelFromReport(customerId, skillId)
                .orElseThrow(() -> new NotFoundException("Report not found for customer with id: " + customerId + " and skill with id: " + skillId));
        List<Recommendation> recommendations = getRecommendationsBySkillLevel(skillLevel, skill);

        // Фильтруем удаленные рекомендации
        List<Recommendation> filteredRecommendations = recommendations.stream()
                .filter(rec -> !rec.isDeleted())
                .toList();

        return filteredRecommendations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Optional<SkillLevel> getSkillLevelFromReport(Long customerId, Long skillId) {
        return Optional.ofNullable(reportRepository.findLatestReportByCustomerIdAndSkillId(customerId, skillId))
                .map(Report::getSkillLevel); // Если отчет не найден, возвращаем Optional.empty
    }

    private List<Recommendation> getRecommendationsBySkillLevel(SkillLevel skillLevel, Skill skill) {
        RecommendationLevel recommendationLevel = convertToRecommendationLevel(skillLevel);
        return recommendationRepository.findByLevelAndSkill(recommendationLevel, skill);
    }

    private RecommendationLevel convertToRecommendationLevel(SkillLevel skillLevel) {
        return switch (skillLevel) {
            case INTERMEDIATE -> RecommendationLevel.INTERMEDIATE;
            case ADVANCED -> RecommendationLevel.ADVANCED;
            default -> RecommendationLevel.BEGINNER;
        };
    }

    @Override
    public List<RecommendationResponse> getAllRecommendations() {
        List<Recommendation> recommendations = recommendationRepository.findAll();
        return recommendations.stream()
                .filter(rec -> !rec.isDeleted()) // Исключаем удаленные рекомендации
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRecommendation(Long recommendationId) {
        Recommendation recommendation = findRecommendationById(recommendationId);
        recommendation.setDeleted(true);
        recommendationRepository.save(recommendation);
    }

    @Override
    public RecommendationResponse updateRecommendationImage(Long recommendationId, MultipartFile image) {
        Recommendation recommendation = findRecommendationById(recommendationId);
        if (image != null) {
            String imagePath = imageService.saveImage(image);
            recommendation.setImagePath(imagePath);
        }Recommendation updatedRecommendation = recommendationRepository.save(recommendation);
        return toResponse(updatedRecommendation);
    }


}

