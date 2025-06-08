package com.example.holing.bounded_context.auth.service;

import com.example.holing.bounded_context.auth.dto.OAuthToken;
import com.example.holing.bounded_context.auth.dto.OAuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final KakaoOAuthService kakaoOAuthService;

    public OAuthUser fetch(String code) {
        OAuthToken oAuthToken = kakaoOAuthService.getToken(code);
        OAuthUser oAuthUser = kakaoOAuthService.getUser(oAuthToken.accessToken());
        return oAuthUser;
    }
}
