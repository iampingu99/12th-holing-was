package com.example.holing.bounded_context.auth.service;

import com.example.holing.bounded_context.auth.dto.OAuthToken;
import com.example.holing.bounded_context.auth.dto.OAuthUser;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final Map<String, OAuthService> OAuthServices;

    public OAuthUser fetch(String provider, String code) {
        OAuthService oAuthService = OAuthServices.get(provider.toUpperCase());
        OAuthToken oAuthToken = oAuthService.getToken(code);
        OAuthUser oAuthUser = oAuthService.getUser(oAuthToken.accessToken());
        return oAuthUser;
    }

    public void unlink(Long socialId) {

    }
}
