package com.aman.ai_demo.config;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.huggingface.HuggingfaceChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class BeansConfig {
    @Bean
    @Profile("ollama")
    public ChatClient ollamaChatClient(
            OllamaChatModel chatModel
    ) {
        return ChatClient.create(chatModel);
    }

    @Bean
    @Profile("hug")
    public ChatClient huggingChatClient(HuggingfaceChatModel chatModel) {
        return ChatClient.create(chatModel);
    }
}
