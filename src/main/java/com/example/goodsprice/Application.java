package com.example.goodsprice;

import com.example.goodsprice.module.llm.config.LlmProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main entry point for the Goods Price Comparison Service.
 *
 * <p>This service extracts product prices from receipt images using OCR,
 * stores them in a database, and helps users find the cheapest goods
 * across multiple stores.</p>
 *
 * @author Dev Team
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableConfigurationProperties(LlmProperties.class)
public class Application {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}