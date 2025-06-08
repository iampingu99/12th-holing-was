package com.example.holing.bounded_context.auth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record KakaoUser(
        Long id,
        KakaoAccount KakaoAccount
) {
    public record KakaoAccount(
            String email,
            Profile profile
    ) {
        public record Profile(
                String nickname,
                String profileImageUrl
        ) {
        }
    }
}
