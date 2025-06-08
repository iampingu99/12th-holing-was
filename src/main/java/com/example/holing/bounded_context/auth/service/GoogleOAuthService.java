package com.example.holing.bounded_context.auth.service;

import com.example.holing.base.config.GoogleProperties;
import com.example.holing.bounded_context.auth.dto.GoogleUser;
import com.example.holing.bounded_context.auth.dto.OAuthToken;
import com.example.holing.bounded_context.auth.dto.OAuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service("GOOGLE")
@RequiredArgsConstructor
public class GoogleOAuthService implements OAuthService {
    private final GoogleProperties googleProperties;
    private final WebClient webClient;

    @Override
    public String getAuthorizeUri() {
        return "";
    }

    @Override
    public OAuthToken getToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", googleProperties.clientId());
        formData.add("client_secret", googleProperties.clientSecret());
        formData.add("redirect_uri", googleProperties.redirectUri());
        formData.add("code", code);

        return webClient.post()
                .uri(googleProperties.endpoints().authUri())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(OAuthToken.class)
                .block();
    }

    @Override
    public OAuthUser getUser(String accessToken) {
        return webClient.get()
                .uri(googleProperties.endpoints().userInfo())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(GoogleUser.class)
                .map(OAuthUser::from)
                .block();
    }

    @Override
    public void unlink(Long socialId) {

    }
}
