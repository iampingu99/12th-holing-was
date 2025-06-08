package com.example.holing.bounded_context.auth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record OAuthToken(
        String accessToken,
        String refreshToken,
        String scope
) {
}
