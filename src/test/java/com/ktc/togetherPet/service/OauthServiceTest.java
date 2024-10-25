package com.ktc.togetherPet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.ktc.togetherPet.jwtUtil.JwtUtil;
import com.ktc.togetherPet.model.dto.oauth.OauthSuccessDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OauthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @InjectMocks
    private OauthService oauthService;

    @Nested
    @DisplayName("로그인 테스트/processOauth")
    class 로그인 {

        @Test
        @DisplayName("유저가 존재하는 경우")
        void 유저가_존재하는_경우() {
            // given
            String email = "test@email.com";
            String expectToken = "testToken";
            OauthSuccessDTO expect = new OauthSuccessDTO(OK, expectToken);

            // when
            when(jwtUtil.makeToken(email))
                .thenReturn(expectToken);

            when(userService.userExists(email))
                .thenReturn(true);

            // then
            assertEquals(expect, oauthService.processOauth(email));

            verify(jwtUtil, times(1))
                .makeToken(email);

            verify(userService, times(1))
                .userExists(email);

            verify(userService, never())
                .createUser(email);
        }

        @Test
        @DisplayName("유저가 존재하지 않는 경우")
        void 유저가_존재하지_않는_경우() {
            // given
            String email = "test@email.com";
            String expectToken = "testToken";
            OauthSuccessDTO expect = new OauthSuccessDTO(CREATED, expectToken);

            // when
            when(jwtUtil.makeToken(email))
                .thenReturn(expectToken);

            when(userService.userExists(email))
                .thenReturn(false);

            // then
            assertEquals(expect, oauthService.processOauth(email));

            verify(jwtUtil, times(1))
                .makeToken(email);

            verify(userService, times(1))
                .userExists(email);

            verify(userService, times(1))
                .createUser(email);
        }
    }
}