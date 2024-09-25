package com.ktc.togetherPet.annotation;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.kakao.KakaoRegisterDTO;
import com.ktc.togetherPet.model.dto.kakao.KakaoUserDTO;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.service.KakaoOauthService;
import com.ktc.togetherPet.service.UserService;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class KakaoUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final KakaoOauthService kakaoOauthService;
    private final UserService userService;

    public KakaoUserArgumentResolver(KakaoOauthService kakaoOauthService, UserService userService) {
        this.kakaoOauthService = kakaoOauthService;
        this.userService = userService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(KakaoUser.class) && KakaoUserDTO.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String authorizationHeader = webRequest.getHeader("Authorization");

        if (isTokenPresent(authorizationHeader)) {
            String accessToken = extractToken(authorizationHeader);
            String email = kakaoOauthService.getEmailFromToken(accessToken);
            User user = userService.findUser(email).user();

            if (user != null) {
                return new KakaoUserDTO();
            }
            else {
                return new KakaoRegisterDTO(accessToken);
            }
        }
        else {
            throw CustomException.invalidHeaderException();
        }
    }

    private boolean isTokenPresent(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }

    private String extractToken(String authorizationHeader) {
        return authorizationHeader.substring(7);
    }

}
