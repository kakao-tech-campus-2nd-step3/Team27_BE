package com.ktc.togetherPet.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ktc.togetherPet.config.KakaoProperties;
import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.kakao.KakaoRegisterDTO;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class KakaoOauthService {
    private final KakaoProperties properties;
    private final UserService userService;

    private RestTemplate client = new RestTemplateBuilder().build();

    public KakaoOauthService(KakaoProperties kakaoProperties, UserService userService) {
        this.properties = kakaoProperties;
        this.userService = userService;
    }

    public void kakaoRegister(KakaoRegisterDTO kakaoRegisterDTO) {
        String email = getEmailFromToken(kakaoRegisterDTO.accessToken());
        userService.createUser(email);
    }

    public String getEmailFromToken(String accessToken) {
        var headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        var request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(properties.userInfoUrl()));
        var response = client.exchange(request, String.class);

        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            HttpStatus httpStatus = HttpStatus.resolve(response.getStatusCode().value());
            throw CustomException.invalidApiException(httpStatus);
        }

        return extractEmail(response.getBody());
    }

    private String extractEmail(String responseBody) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
        JsonObject kakaoAccount = jsonObject.getAsJsonObject("kakao_account");

        return kakaoAccount.get("email").getAsString();
    }
}
