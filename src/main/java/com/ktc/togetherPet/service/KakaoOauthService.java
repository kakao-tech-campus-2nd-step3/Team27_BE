package com.ktc.togetherPet.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ktc.togetherPet.config.property.KakaoProperties;
import com.ktc.togetherPet.exception.CustomException;
import java.net.URI;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoOauthService {

    private final KakaoProperties properties;

    private RestTemplate client = new RestTemplateBuilder().build();

    public KakaoOauthService(KakaoProperties kakaoProperties) {
        this.properties = kakaoProperties;
    }

    public String getEmailFromToken(String accessToken) {
        var headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        var request = new RequestEntity<>(headers, HttpMethod.GET,
            URI.create(properties.userInfoUrl()));
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
