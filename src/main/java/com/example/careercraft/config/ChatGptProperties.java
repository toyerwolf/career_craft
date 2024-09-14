package com.example.careercraft.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chatgpt.api")
@Data
public class ChatGptProperties {
    private String key;

}