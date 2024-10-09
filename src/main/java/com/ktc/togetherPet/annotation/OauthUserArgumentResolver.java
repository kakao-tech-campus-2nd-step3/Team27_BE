package com.ktc.togetherPet.annotation;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.jwtUtil.JwtUtil;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


@Component
@RequiredArgsConstructor
public class OauthUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(OauthUser.class)
            && (OauthUserDTO.class.isAssignableFrom(parameter.getParameterType()));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        String authorizationHeader = webRequest.getHeader("Authorization");
        validateAuthorizationHeader(authorizationHeader);

        String jwtToken = extractToken(authorizationHeader);
        validateToken(jwtToken);

        String email = jwtUtil.getSubject(jwtToken);
        validateUserExists(email);

        return new OauthUserDTO(email);
    }

    private void validateAuthorizationHeader(String authorizationHeader) {
        if (!isTokenPresent(authorizationHeader)) {
            throw CustomException.invalidHeaderException();
        }
    }

    private void validateToken(String jwtToken) {
        if (!jwtUtil.validateToken(jwtToken)) {
            throw CustomException.invalidTokenException();
        }
    }

    private void validateUserExists(String email) {
        if (!userService.userExists(email)) {
            throw CustomException.invalidTokenException();
        }
    }

    private boolean isTokenPresent(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }

    private String extractToken(String authorizationHeader) {
        return authorizationHeader.substring(7);
    }
}

