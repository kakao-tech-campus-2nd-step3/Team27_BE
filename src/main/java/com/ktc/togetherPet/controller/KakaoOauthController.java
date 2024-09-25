package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.annotation.KakaoUser;
import com.ktc.togetherPet.model.dto.kakao.KakaoRegisterDTO;
import com.ktc.togetherPet.model.dto.kakao.KakaoUserDTO;
import com.ktc.togetherPet.service.KakaoOauthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/login/kakao")
public class KakaoOauthController {
    private final KakaoOauthService kakaoOauthService;

    public KakaoOauthController(KakaoOauthService kakaoOauthService) {
        this.kakaoOauthService = kakaoOauthService;
    }

    @GetMapping
    public ResponseEntity<?> kakaoOauth(@KakaoUser Object kakaoOauthDTO) {
        if (kakaoOauthDTO instanceof KakaoUserDTO) {
            return ResponseEntity.ok().build();
        }

        if (kakaoOauthDTO instanceof KakaoRegisterDTO) {
            kakaoOauthService.kakaoRegister((KakaoRegisterDTO) kakaoOauthDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
