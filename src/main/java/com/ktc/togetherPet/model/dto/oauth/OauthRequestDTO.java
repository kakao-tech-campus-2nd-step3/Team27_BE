package com.ktc.togetherPet.model.dto.oauth;

import com.ktc.togetherPet.annotation.ValidEmail;

public record OauthRequestDTO(
    @ValidEmail
    String email) {

}
