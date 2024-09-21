package com.example.careercraft.service.impl;

import com.example.careercraft.config.ChatGptProperties;
import com.example.careercraft.exception.AppException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

@Service
public class ChatGptService {


        private final ChatGptProperties chatGptProperties;
        private final RestTemplate restTemplate;
        private final ObjectMapper objectMapper;

        public ChatGptService(ChatGptProperties chatGptProperties, RestTemplate restTemplate, ObjectMapper objectMapper) {
            this.chatGptProperties = chatGptProperties;
            this.restTemplate = restTemplate;
            this.objectMapper = objectMapper;
        }

    public String sendMessage(String message) {
        String apiEndpoint = "https://api.openai.com/v1/chat/completions"; // Updated endpoint for gpt-3.5-turbo
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + chatGptProperties.getKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the request body for gpt-3.5-turbo
        ObjectNode requestBodyNode = objectMapper.createObjectNode();
        requestBodyNode.put("model", "babbage-002");
        requestBodyNode.putArray("messages").addObject().put("role", "user").put("content", message);
        requestBodyNode.put("max_tokens", 150);

        HttpEntity<String> request = new HttpEntity<>(requestBodyNode.toString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiEndpoint, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                try {
                    JsonNode jsonNode = objectMapper.readTree(response.getBody());
                    return jsonNode.path("choices").get(0).path("message").path("content").asText();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse response from OpenAI: " + e.getMessage());
                }
            } else {
                throw new RuntimeException("Failed to get response from OpenAI. Status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            // Log and handle HTTP client errors
            String errorResponse = e.getResponseBodyAsString();
            throw new RuntimeException("Request to OpenAI failed with status code " + e.getStatusCode() + ". Response: " + errorResponse, e);
        } catch (Exception e) {
            // Log and handle other exceptions
            throw new RuntimeException("Unexpected error occurred during request to OpenAI: " + e.getMessage(), e);
        }
    }
}



