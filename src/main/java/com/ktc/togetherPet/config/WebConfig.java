package com.ktc.togetherPet.config;

import com.ktc.togetherPet.annotation.KakaoUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final KakaoUserArgumentResolver kakaoUserArgumentResolver;

    public WebConfig(KakaoUserArgumentResolver kakaoUserArgumentResolver) {
        this.kakaoUserArgumentResolver = kakaoUserArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(kakaoUserArgumentResolver);
    }
}
