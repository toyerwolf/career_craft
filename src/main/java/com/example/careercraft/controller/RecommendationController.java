package com.example.careercraft.controller;

import com.example.careercraft.dto.CustomerInfo;
import com.example.careercraft.response.RecommendationResponse;
import com.example.careercraft.req.RecommendationRequest;
import com.example.careercraft.service.AuthService;
import com.example.careercraft.service.RecommendationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@AllArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final AuthService authService;

    // Добавление новой рекомендации

    @PostMapping
    public ResponseEntity<RecommendationResponse> addRecommendation(
            @ModelAttribute RecommendationRequest recommendationRequest, // данные рекомендации
            @RequestParam(value = "image", required = false) MultipartFile image // файл изображения
    ) {
        RecommendationResponse response = recommendationService.addRecommendation(recommendationRequest, image);
        return ResponseEntity.ok(response); // возвращаем успешный ответ с данными рекомендации
    }


    @GetMapping("/skill/{skillId}")
    @Secured("USER")
    public ResponseEntity<List<RecommendationResponse>> getRecommendationsForCustomer(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Long skillId) {
        CustomerInfo customerInfo = authService.getCustomerDetailsFromToken(authHeader);
        Long customerId = customerInfo.getId();
        List<RecommendationResponse> recommendations = recommendationService.getRecommendationsForCustomer(customerId, skillId);
        return ResponseEntity.ok(recommendations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecommendationResponse> updateRecommendation(
            @PathVariable("id") Long recommendationId,
            @ModelAttribute("recommendation") RecommendationRequest recommendationRequest,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        RecommendationResponse response = recommendationService.updateRecommendation(recommendationId, recommendationRequest, image);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable("id") Long recommendationId) {
        recommendationService.deleteRecommendation(recommendationId);
        return ResponseEntity.noContent().build(); // Возвращаем статус 204 No Content
    }

    @PatchMapping("/{id}/image")
    public ResponseEntity<RecommendationResponse> updateImage(
            @PathVariable("id") Long recommendationId,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        RecommendationResponse response = recommendationService.updateRecommendationImage(recommendationId, image);
        return ResponseEntity.ok(response);
    }


    // Получение всех рекомендаций (опционально, если нужно для администрирования)
    @GetMapping
    public ResponseEntity<List<RecommendationResponse>> getAllRecommendations() {
        List<RecommendationResponse> recommendations = recommendationService.getAllRecommendations();
        return ResponseEntity.ok(recommendations);
    }



}