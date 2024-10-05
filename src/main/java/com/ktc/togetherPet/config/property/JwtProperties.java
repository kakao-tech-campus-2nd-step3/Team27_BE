package com.ktc.togetherPet.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record JwtProperties(String secretKey) {

}
