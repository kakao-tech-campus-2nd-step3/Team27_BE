package com.ktc.togetherPet.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.user.UserInfoResponseDTO;
import com.ktc.togetherPet.service.UserService;
import com.ktc.togetherPet.testConfig.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(UserController.class)
class UserControllerTest extends RestDocsTestSupport {

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("토큰을 바탕으로 사용자 정보를 받아오는 테스트/getUserInfo")
    void 토큰을_바탕으로_사용자_정보_받아오기() throws Exception {
        // given
        String givenToken = "Bearer testToken";
        OauthUserDTO expectOauthUserDTO = new OauthUserDTO("test@email.com");
        UserInfoResponseDTO expectUserInfoResponseDTO = new UserInfoResponseDTO(
            "testUserName",
            "testPetName",
            "https://together-pet/v1/images/test-uuid",
            1L
        );

        // when
        when(oauthUserArgumentResolver.supportsParameter(any()))
            .thenReturn(true);

        when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
            .thenReturn(expectOauthUserDTO);

        when(userService.getUserInfo(expectOauthUserDTO))
            .thenReturn(expectUserInfoResponseDTO);

        ResultActions result = mockMvc.perform(
            get("/api/v1/user/info")
                .header("Authorization", givenToken)
        );
        // then
        result.andExpectAll(
                status().isOk(),
                content().contentType(APPLICATION_JSON),
                content().json(toJson(expectUserInfoResponseDTO))
            )
            .andDo(restDocs.document(
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer을 포함한 토큰")
                    ),
                    responseFields(
                        fieldWithPath("user_name").description("사용자 이름"),
                        fieldWithPath("pet_name").description("애완동물 이름"),
                        fieldWithPath("pet_image_url").description("애완동물의 대표 이미지"),
                        fieldWithPath("pet_birth_month").description("애완동물의 개월수")
                    )
                )
            );

        verify(userService, times(1))
            .getUserInfo(expectOauthUserDTO);
    }

}