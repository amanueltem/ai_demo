package com.aman.ai_demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class AiDemoApplication {

	public static void main(String[] args) {
	    // Load .env and force them into System properties
	    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
	    dotenv.entries().forEach(entry -> {
	        System.setProperty(entry.getKey(), entry.getValue());
	    });

	    SpringApplication.run(AiDemoApplication.class, args);
	}
}
