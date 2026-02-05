package com.aman.ai_demo.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class DotenvConfig {

    static {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        
        // Loop through all entries and only set if the value is NOT null
        dotenv.entries().forEach(entry -> {
            if (entry.getValue() != null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });
    }
}

