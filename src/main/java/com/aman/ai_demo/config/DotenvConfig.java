package com.aman.ai_demo.config;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class DotenvConfig {

    static {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("OPENAI_API_KEY", Objects.requireNonNull(dotenv.get("OPENAI_API_KEY")));
        System.setProperty("HUG_API_KEY", Objects.requireNonNull(dotenv.get("HUG_API_KEY")));
    }
}

