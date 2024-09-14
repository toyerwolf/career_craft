package com.example.careercraft.service.impl;

import com.example.careercraft.config.ChatGptProperties;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

@Service
public class ChatGptService {

    private final WebClient webClient;
    @Autowired
    public ChatGptService(ChatGptProperties chatGptProperties, WebClient.Builder webClientBuilder) {
        String apiKey = chatGptProperties.getKey();
        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com/v1/engines/davinci-codex/completions")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public CompletableFuture<String> getResponse(String prompt) {
        return webClient.post()
                .bodyValue(new ChatGptRequest(prompt))
                .retrieve()
                .bodyToMono(ChatGptResponse.class)
                .map(ChatGptResponse::text)
                .toFuture();
    }



        private record ChatGptRequest(String prompt) {
    }


        private record ChatGptResponse(String text) {
    }
}