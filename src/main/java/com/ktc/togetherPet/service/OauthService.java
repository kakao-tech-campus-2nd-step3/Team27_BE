package com.ktc.togetherPet.service;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.jwtUtil.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class OauthService {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final KakaoOauthService kakaoOauthService;

    public OauthService(JwtUtil jwtUtil, UserService userService,
        KakaoOauthService kakaoOauthService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.kakaoOauthService = kakaoOauthService;
    }

    public String processOauth(String provider, String accessToken) {
        String email = getEmailFromProvider(provider, accessToken);
        ensureUserExists(email);

        return jwtUtil.makeToken(email);
    }

    private void ensureUserExists(String email) {
        if (!userService.userExists(email)) {
            userService.createUser(email);
        }
    }

    private String getEmailFromProvider(String provider, String accessToken) {
        if (provider.equals("kakao")) {
            return kakaoOauthService.getEmailFromToken(accessToken);
        }

        throw CustomException.invalidProviderException();
    }
}
