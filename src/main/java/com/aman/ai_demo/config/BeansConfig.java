package com.aman.ai_demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class BeansConfig {
	@Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        // This 'builder' is auto-configured by Spring using your YML settings.
        // We just call .build() to create the ChatClient bean your service needs.
        return builder.build();
    }
}