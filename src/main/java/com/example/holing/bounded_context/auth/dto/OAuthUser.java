package com.example.holing.bounded_context.auth.dto;

public record OAuthUser(
        Long id,
        String email,
        String nickname,
        String profileImageUrl
) {
    public static OAuthUser from(KakaoUser kakaoUser) {
        return new OAuthUser(
                kakaoUser.id(),
                kakaoUser.KakaoAccount().email(),
                kakaoUser.KakaoAccount().profile().nickname(),
                kakaoUser.KakaoAccount().profile().profileImageUrl()
        );
    }
}
