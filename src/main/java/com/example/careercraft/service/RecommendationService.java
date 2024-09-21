package com.example.careercraft.service;

import com.example.careercraft.response.RecommendationResponse;
import com.example.careercraft.req.RecommendationRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecommendationService {

    public RecommendationResponse addRecommendation(RecommendationRequest recommendationRequest, MultipartFile image);

    public List<RecommendationResponse> getRecommendationsForCustomer(Long customerId, Long skillId);

    public void deleteRecommendation(Long recommendationId);

    public List<RecommendationResponse> getAllRecommendations();

    RecommendationResponse updateRecommendation(Long recommendationId, RecommendationRequest recommendationRequest, MultipartFile image);


    public RecommendationResponse updateRecommendationImage(Long recommendationId, MultipartFile image);
}
