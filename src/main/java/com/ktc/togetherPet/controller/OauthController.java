package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.apiResponse.CustomResponse;
import com.ktc.togetherPet.model.dto.oauth.OauthRequestDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthSuccessDTO;
import com.ktc.togetherPet.service.OauthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    @GetMapping
    public ResponseEntity<?> handleOauth(
        @Valid @RequestBody OauthRequestDTO oauthRequestDTO
    ) {

        OauthSuccessDTO oauthSuccessDTO = oauthService.processOauth(oauthRequestDTO.email());

        return CustomResponse.oauthSuccess(oauthSuccessDTO);
    }

}