package com.example.holing.base.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google")
public record GoogleProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        Endpoints endpoints
) {
    public record Endpoints(
            String authUri,
            String userInfo
    ) {
    }
}
