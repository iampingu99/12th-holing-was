package com.example.holing.bounded_context.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoProperties(
        String authorizationUri,
        String tokenUri,
        String userInfoUri,
        String unlinkUri,
        String clientId,
        String clientSecret,
        String adminKey,
        String redirectUri,
        String scope,
        Endpoints endpoints
) {
    public record Endpoints(
            String oAuthToken,
            String userMe
    ) {
    }
}
