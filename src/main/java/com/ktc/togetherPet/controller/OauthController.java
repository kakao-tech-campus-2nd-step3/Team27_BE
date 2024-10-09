package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.apiResponse.CustomResponse;
import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.oauth.OauthSuccessDTO;
import com.ktc.togetherPet.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v0/login")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    @GetMapping
    public ResponseEntity<?> handleOauth(@RequestHeader("Authorization") String authorizationHeader) {

        String email = extractEmail(authorizationHeader);
        OauthSuccessDTO oauthSuccessDTO = oauthService.processOauth(email);

        return CustomResponse.oauthSuccess(oauthSuccessDTO);
    }

    private String extractEmail(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }

        throw CustomException.invalidHeaderException();
    }
}