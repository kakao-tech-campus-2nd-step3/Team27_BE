package com.ktc.togetherPet.model.dto.oauth;

import org.springframework.http.HttpStatus;

public record OauthSuccessDTO(HttpStatus status, String jwtToken) {

}
