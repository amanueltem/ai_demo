package com.aman.ai_demo;

import org.springframework.ai.model.ollama.autoconfigure.OllamaEmbeddingAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(exclude = {
		OllamaEmbeddingAutoConfiguration.class
})
public class AiDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiDemoApplication.class, args);
	}

}
