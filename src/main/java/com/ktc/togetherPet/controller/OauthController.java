package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.apiResponse.CustomResponse;
import com.ktc.togetherPet.model.dto.oauth.OauthRequestDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthSuccessDTO;
import com.ktc.togetherPet.service.OauthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    @PostMapping
    public ResponseEntity<?> handleOauth(
        @Valid @RequestBody OauthRequestDTO oauthRequestDTO
    ) {

        OauthSuccessDTO oauthSuccessDTO = oauthService.processOauth(oauthRequestDTO.email());

        return CustomResponse.oauthSuccess(oauthSuccessDTO);
    }

}