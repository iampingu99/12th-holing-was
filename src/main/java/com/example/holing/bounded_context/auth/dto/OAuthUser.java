package com.example.holing.bounded_context.auth.dto;

public record OAuthUser(
        String id,
        String email,
        String nickname,
        String profileImageUrl,
        String provider
) {
    public static OAuthUser from(KakaoUser kakaoUser) {
        return new OAuthUser(
                kakaoUser.id().toString(),
                kakaoUser.KakaoAccount().email(),
                kakaoUser.KakaoAccount().profile().nickname(),
                kakaoUser.KakaoAccount().profile().profileImageUrl(),
                "KAKAO"
        );
    }

    public static OAuthUser from(GoogleUser googleUser) {
        return new OAuthUser(
                googleUser.sub(),
                googleUser.email(),
                googleUser.nickname(),
                googleUser.picture(),
                "GOOGLE"
        );
    }
}
