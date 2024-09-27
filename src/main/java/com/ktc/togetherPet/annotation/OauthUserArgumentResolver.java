package com.ktc.togetherPet.annotation;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.oauth.OauthRegisterDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
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
public class OauthUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final KakaoOauthService kakaoOauthService;
    private final UserService userService;

    public OauthUserArgumentResolver(KakaoOauthService kakaoOauthService, UserService userService) {
        this.kakaoOauthService = kakaoOauthService;
        this.userService = userService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(OauthUser.class)
                && (OauthUserDTO.class.isAssignableFrom(parameter.getParameterType())
                || OauthRegisterDTO.class.isAssignableFrom(parameter.getParameterType()));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String authorizationHeader = webRequest.getHeader("Authorization");
        String provider = (String) webRequest.getAttribute("provider", NativeWebRequest.SCOPE_REQUEST);

        if (isTokenPresent(authorizationHeader)) {
            String accessToken = extractToken(authorizationHeader);
            String email = getEmailFromProvider(provider, accessToken);
            User user = userService.findUser(email).user();

            if (user != null) {
                return new OauthUserDTO(email);
            }
            else {
                return new OauthRegisterDTO(email);
            }
        }
        else {
            throw CustomException.invalidHeaderException();
        }
    }

    private String getEmailFromProvider(String provider, String accessToken) {
        if ("kakao".equals(provider)) {
            return kakaoOauthService.getEmailFromToken(accessToken);
        }

        throw CustomException.invalidProviderException();
    }

    private boolean isTokenPresent(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }

    private String extractToken(String authorizationHeader) {
        return authorizationHeader.substring(7);
    }
}

