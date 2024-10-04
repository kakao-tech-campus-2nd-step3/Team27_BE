package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.service.OauthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/login")
public class OauthController {

    private final OauthService oauthService;

    public OauthController(OauthService oauthService) {
        this.oauthService = oauthService;
    }

    @GetMapping("/{provider}")
    public ResponseEntity<?> handleOauth(@RequestHeader("Authorization") String authorizationHeader,
        @PathVariable String provider) {

        String accessToken = extractAccessToken(authorizationHeader);
        String jwtToken = oauthService.processOauth(provider, accessToken);

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .build();
    }

    private String extractAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }

        throw CustomException.invalidHeaderException();
    }
}