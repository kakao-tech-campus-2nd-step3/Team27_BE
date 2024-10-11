package com.ktc.togetherPet.service;

import com.ktc.togetherPet.jwtUtil.JwtUtil;
import lombok.RequiredArgsConstructor;
import com.ktc.togetherPet.model.dto.oauth.OauthSuccessDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public OauthSuccessDTO processOauth(String email) {
        HttpStatus status = ensureUserExists(email);

        return new OauthSuccessDTO(status, jwtUtil.makeToken(email));
    }

    private HttpStatus ensureUserExists(String email) {
        if (!userService.userExists(email)) {
            userService.createUser(email);
            return HttpStatus.CREATED;
        }

        return HttpStatus.OK;
    }
}
