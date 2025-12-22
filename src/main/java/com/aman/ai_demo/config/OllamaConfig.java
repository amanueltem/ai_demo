package com.aman.ai_demo.config;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.web.client.RestClient; // Import RestClient
import org.springframework.web.reactive.function.client.WebClient; // Import WebClient

@Configuration
public class OllamaConfig {

    private static final String OLLAMA_BASE_URL = "http://localhost:11434";

    /**
     * 1. MANUALLY DEFINE OLLAMA API BEAN using the public Builder pattern.
     * This bypasses the private constructor restriction and explicitly sets the base URL.
     */
    @Bean
    @Primary
    public OllamaApi ollamaApi(
            RestClient.Builder restClientBuilder, // Injected by Spring Boot
            WebClient.Builder webClientBuilder    // Injected by Spring Boot
    ) {
        return OllamaApi.builder() // Use the static factory method
                .baseUrl(OLLAMA_BASE_URL)
                // Pass the injected builders to the API builder
                .restClientBuilder(restClientBuilder)
                .webClientBuilder(webClientBuilder)
                .build();
    }

    /**
     * 2. Define a NOOP ObservationRegistry bean (if not provided elsewhere).
     */
    @Bean
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.NOOP;
    }

    /**
     * 3. Manually define the OllamaEmbeddingModel bean.
     */
    @Bean
    public OllamaEmbeddingModel ollamaEmbeddingModel(
            OllamaApi ollamaApi, // Uses YOUR custom, correctly configured OllamaApi
            ObservationRegistry observationRegistry
    ) {
        OllamaEmbeddingOptions defaultOptions = OllamaEmbeddingOptions.builder()
                .model("nomic-embed-text") // Replace with your model if different
                .build();

        return OllamaEmbeddingModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(defaultOptions)
                .observationRegistry(observationRegistry)
                .modelManagementOptions(ModelManagementOptions.defaults())
                .build();
    }
}