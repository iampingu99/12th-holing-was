package com.example.holing.bounded_context.auth.dto;

public record OAuthUserInfoDto(
        Long id,
        String nickname,
        String email,
        String profileImageUrl
) {
    public static OAuthUserInfoDto from(KakaoUser kakaoUser) {
        return new OAuthUserInfoDto(
                kakaoUser.id(),
                kakaoUser.KakaoAccount().email(),
                kakaoUser.KakaoAccount().profile().nickname(),
                kakaoUser.KakaoAccount().profile().profileImageUrl()
        );
    }
}
