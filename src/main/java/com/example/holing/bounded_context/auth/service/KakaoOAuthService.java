package com.example.holing.bounded_context.auth.service;

import com.example.holing.bounded_context.auth.config.KakaoProperties;
import com.example.holing.bounded_context.auth.dto.OAuthTokenInfoDto;
import com.example.holing.bounded_context.auth.dto.OAuthUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService implements OAuthService {
    private final WebClient webClient;
    private final KakaoProperties kakaoProperties;

    /**
     * 인가 코드 요청 uri 생성 : 인가 코드를 요청하는 uri 를 생성하여 반환합니다.<br>
     *
     * @return uri
     */
    @Override
    public String getAuthorizeUri() {
        return UriComponentsBuilder.fromHttpUrl(kakaoProperties.authorizationUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoProperties.clientId())
                .queryParam("redirect_uri", kakaoProperties.redirectUri())
                .queryParam("scope", kakaoProperties.scope())
                .toUriString();
    }

    /**
     * 토큰 받기 : 인가 코드로 토큰 발급을 요청합니다.<br> https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token
     *
     * @param code 인가 코드
     * @return TokenInfoDto 토큰 정보
     * @throws HttpClientErrorException api 요청 실패 시 발생합니다.
     */
    @Override
    public OAuthTokenInfoDto getToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", kakaoProperties.clientId());
        formData.add("redirect_uri", kakaoProperties.redirectUri());
        formData.add("code", code);

        return webClient.post()
                .uri(kakaoProperties.endpoints().oAuthToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(OAuthTokenInfoDto.class)
                .block();
    }

    /**
     * 사용자 정보 받기 : 현재 로그인한 사용자의 정보를 불러옵니다.<br>
     * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
     *
     * @param accessToken 소셜 인증 서버 access token
     * @return UserInfoDto 유저 정보
     * @throws HttpClientErrorException api 요청 실패 시 발생합니다.
     */
    @Override
    public OAuthUserInfoDto getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(kakaoProperties.userInfoUri(), HttpMethod.GET,
                entity, String.class);

        JSONObject userInfoJson = new JSONObject(response.getBody());
        Long id = userInfoJson.getLong("id");
        String email = userInfoJson.getJSONObject("kakao_account").getString("email");
        String nickname = userInfoJson.getJSONObject("kakao_account").getJSONObject("profile").getString("nickname");
        String profileImageUrl = userInfoJson.getJSONObject("kakao_account").getJSONObject("profile")
                .getString("profile_image_url");

        return OAuthUserInfoDto.of(id, nickname, email, profileImageUrl);
    }

    /**
     * 연결 끊기 : 앱과 사용자 카카오계정의 연결을 끊는 기능입니다.(서비스 앱 어드민 키 방식)<br>
     * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#unlink-request-admin-key
     *
     * @param socialId
     * @throws HttpClientErrorException api 요청 실패 시 발생합니다.
     */
    @Override
    public void unlink(Long socialId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + kakaoProperties.adminKey());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", socialId.toString());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        restTemplate.postForEntity(kakaoProperties.unlinkUri(), entity, String.class);
    }
}
