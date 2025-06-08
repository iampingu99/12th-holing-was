package com.example.holing.bounded_context.auth.service;

import com.example.holing.bounded_context.auth.dto.OAuthToken;
import com.example.holing.bounded_context.auth.dto.OAuthUser;

public interface OAuthService {
    String getAuthorizeUri();

    OAuthToken getToken(String code);

    OAuthUser getUser(String accessToken);

    void unlink(Long socialId);
}
