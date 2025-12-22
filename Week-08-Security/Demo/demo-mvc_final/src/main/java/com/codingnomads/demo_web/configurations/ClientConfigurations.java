package com.codingnomads.demo_web.configurations;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration for HTTP clients used to call external APIs.
 */
@Configuration
public class ClientConfigurations {

    /**
     * RestTemplate is a synchronous client to perform HTTP requests.
     * We define it as a Bean so it can be reused throughout the application.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                // Set a timeout of 1000ms so the app doesn't hang if the external API is slow
                .connectTimeout(Duration.ofMillis(1000))
                .build();
    }

}
