package com.example.careercraft.controller;

import com.example.careercraft.service.impl.ChatGptService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {

    private final ChatGptService chatGptService;



    @PostMapping("/message")
    public String sendMessage(@RequestBody String message) {
        return chatGptService.sendMessage(message);
    }
}