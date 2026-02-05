package com.aman.ai_demo.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class DotenvConfig {

   static {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("GROQ_API_KEY", Objects.requireNonNull(dotenv.get("GROQ_API_KEY")));
        System.setProperty("VOYAGE_API_KEY",Objects.requireNonNull(dotenv.get("VOYAGE_API_KEY")));
    }
}

