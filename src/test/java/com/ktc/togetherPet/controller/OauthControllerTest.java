package com.ktc.togetherPet.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ktc.togetherPet.annotation.OauthUserArgumentResolver;
import com.ktc.togetherPet.model.dto.oauth.OauthSuccessDTO;
import com.ktc.togetherPet.service.OauthService;
import com.ktc.togetherPet.testConfig.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    @DisplayName("로그인 테스트/handleOauth")
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
            get("/api/v0/login")
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
}