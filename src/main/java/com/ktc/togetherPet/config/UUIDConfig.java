package com.ktc.togetherPet.config;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UUIDConfig {

    @Bean
    public UUIDGenerator uuidGenerator() {
        return new UUIDGenerator();
    }
}
