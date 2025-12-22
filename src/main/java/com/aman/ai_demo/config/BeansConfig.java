package com.aman.ai_demo.config;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        // Use the auto-configured ChatModel based on active profile
        return ChatClient.builder(chatModel)
                .defaultSystem("You are a helpful AI assistant.")
                .build();
    }

}
