package com.ktc.togetherPet;

import com.ktc.togetherPet.config.ImageConfig;
import com.ktc.togetherPet.config.property.JwtProperties;
import com.ktc.togetherPet.config.property.KakaoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, KakaoProperties.class, ImageConfig.class})
public class TogetherPetApplication {

    public static void main(String[] args) {
        SpringApplication.run(TogetherPetApplication.class, args);
    }

}
