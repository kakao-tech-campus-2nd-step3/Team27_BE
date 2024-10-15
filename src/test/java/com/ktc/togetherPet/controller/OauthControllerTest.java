package com.ktc.togetherPet.controller;

import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_HEADER;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ktc.togetherPet.annotation.OauthUserArgumentResolver;
import com.ktc.togetherPet.model.dto.oauth.OauthSuccessDTO;
import com.ktc.togetherPet.service.OauthService;
import com.ktc.togetherPet.testConfig.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(OauthController.class)
class OauthControllerTest extends RestDocsTestSupport {

    @MockBean
    private OauthService oauthService;

    @MockBean
    private OauthUserArgumentResolver oauthUserArgumentResolver;

    @Test
    @DisplayName("로그인 테스트 이미 존재하는 사용자의 경우/handleOauth")
    void 이미_존재하는_사용자() throws Exception {
        // given
        String inputEmail = "Bearer test@email.com";
        String extractedEmail = "test@email.com";
        String token = "testToken";
        OauthSuccessDTO actual = new OauthSuccessDTO(OK, token);

        // when
        when(oauthService.processOauth(extractedEmail))
            .thenReturn(actual);

        ResultActions result = mockMvc.perform(
            get("/api/v1/login")
                .header("Authorization", inputEmail)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andExpectAll(
            status().isOk(),
            header().string("Authorization", "Bearer " + token)
        ).andDo(restDocs.document(
            requestHeaders(
                headerWithName("Authorization").description("Bearer을 포함한 이메일")
            ),
            responseHeaders(
                headerWithName("Authorization").description("Bearer을 포함한 토큰")
            )
        ));

        verify(oauthService, times(1))
            .processOauth(extractedEmail);
    }

    @Test
    @DisplayName("로그인 테스트 존재하지 않는 사용자의 경우/handleOauth")
    void 존재하지_않는_사용자() throws Exception {
        // given
        String inputEmail = "Bearer test@email.com";
        String extractedEmail = "test@email.com";
        String token = "testToken";
        OauthSuccessDTO actual = new OauthSuccessDTO(CREATED, token);

        // when
        when(oauthService.processOauth(extractedEmail))
            .thenReturn(actual);

        ResultActions result = mockMvc.perform(
            get("/api/v1/login")
                .header("Authorization", inputEmail)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andExpectAll(
            status().isCreated(),
            header().string("Authorization", "Bearer " + token)
        ).andDo(restDocs.document(
            requestHeaders(
                headerWithName("Authorization").description("Bearer을 포함한 이메일")
            ),
            responseHeaders(
                headerWithName("Authorization").description("Bearer을 포함한 토큰")
            )
        ));

        verify(oauthService, times(1))
            .processOauth(extractedEmail);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "test@email"})
    @DisplayName("로그인 테스트 입력된 이메일이 잘못된 경우/handleOauth")
    void 잘못된_이메일(String inputEmail) throws Exception {
        // when
        ResultActions result = mockMvc.perform(
            get("/api/v1/login")
                .header("Authorization", inputEmail)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andExpectAll(
            status().isBadRequest(),
            content().contentType(APPLICATION_JSON),
            content().json(toJson(INVALID_HEADER))
        ).andDo(restDocs.document(
            requestHeaders(
                headerWithName("Authorization").description("Bearer을 포함한 이메일")
            ),
            responseFields(
                fieldWithPath("code").description("오류 코드"),
                fieldWithPath("message").description("오류에 대한 설명")
            )
        ));

        verify(oauthService, never())
            .processOauth(inputEmail);
    }
}