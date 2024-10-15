package com.ktc.togetherPet.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "image")
public record ImageConfig(
    String folderPath,
    String sourcePrefix
) {

}
