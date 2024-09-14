package com.example.careercraft.config;

import com.example.careercraft.service.impl.ChatGptService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;


@Component
@RequiredArgsConstructor
public class ChatHandler extends TextWebSocketHandler {

    private final ChatGptService chatGptService;

    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws IOException {
        CompletableFuture<String> chatGptResponse = chatGptService.getResponse(message.getPayload());
        chatGptResponse.thenAccept(response -> {
            try {
                session.sendMessage(new TextMessage(response));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
