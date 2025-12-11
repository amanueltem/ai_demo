package com.aman.ai_demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {
    @Bean
    public ChatClient anthropicChatClient(OllamaChatModel chatModel) {
        return ChatClient.create(chatModel);
    }
}
