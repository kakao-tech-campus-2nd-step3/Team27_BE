package com.ktc.togetherPet.config;

import com.ktc.togetherPet.annotation.OauthUserArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final OauthUserArgumentResolver oauthUserArgumentResolver;

    public WebConfig(OauthUserArgumentResolver oauthUserArgumentResolver) {
        this.oauthUserArgumentResolver = oauthUserArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(oauthUserArgumentResolver);
    }
}
