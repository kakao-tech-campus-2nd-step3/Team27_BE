package com.ktc.togetherPet.controller;

import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_EMAIL_FORMAT;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ktc.togetherPet.annotation.OauthUserArgumentResolver;
import com.ktc.togetherPet.jwtUtil.JwtUtil;
import com.ktc.togetherPet.model.dto.oauth.OauthRequestDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthSuccessDTO;
import com.ktc.togetherPet.service.OauthService;
import com.ktc.togetherPet.testConfig.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(OauthController.class)
class OauthControllerTest2 extends RestDocsTestSupport {

    @MockBean
    private OauthService oauthService;

    @MockBean
    private OauthUserArgumentResolver oauthUserArgumentResolver;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("로그인 테스트 이미 존재하는 사용자의 경우/handleOauth")
    void 이미_존재하는_사용자() throws Exception {
        // given
        String inputEmail = "test@email.com";
        String token = "testToken";
        OauthRequestDTO oauthRequestDTO = new OauthRequestDTO(inputEmail);
        OauthSuccessDTO actual = new OauthSuccessDTO(HttpStatus.OK, token); // 응답 DTO의 상태도 적절히 설정

        // when
        when(oauthService.processOauth(inputEmail)).thenReturn(actual);

        ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/login") // POST 요청으로 수정
                .contentType(APPLICATION_JSON)
                .content(toJson(oauthRequestDTO))
        );

        // then
        result.andExpectAll(
            status().isOk(),
            header().string("Authorization", "Bearer " + token) // JWT 토큰 확인
        ).andDo(restDocs.document(
            responseHeaders(
                headerWithName("Authorization").description("Bearer을 포함한 토큰")
            )
        ));

        verify(oauthService, times(1))
            .processOauth(inputEmail);
    }

    @Test
    @DisplayName("로그인 테스트 존재하지 않는 사용자의 경우/handleOauth")
    void 존재하지_않는_사용자() throws Exception {
        // given
        String inputEmail = "test@email.com";
        String token = "testToken";
        OauthRequestDTO oauthRequestDTO = new OauthRequestDTO(inputEmail);
        OauthSuccessDTO actual = new OauthSuccessDTO(HttpStatus.CREATED, token); // CREATED 상태 설정

        // when
        when(oauthService.processOauth(inputEmail)).thenReturn(actual);

        ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/login") // POST 요청으로 변경
                .contentType(APPLICATION_JSON)
                .content(toJson(oauthRequestDTO)) // 요청 본문에 DTO JSON 추가
        );

        // then
        result.andExpectAll(
            status().isCreated(),
            header().string("Authorization", "Bearer " + token) // JWT 토큰 확인
        ).andDo(restDocs.document(
            responseHeaders(
                headerWithName("Authorization").description("Bearer을 포함한 토큰")
            )
        ));

        verify(oauthService, times(1)).processOauth(inputEmail);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "invalid-email"})
    @DisplayName("로그인 테스트 입력된 이메일이 잘못된 경우/handleOauth")
    void 잘못된_이메일(String inputEmail) throws Exception {
        // given
        OauthRequestDTO oauthRequestDTO = new OauthRequestDTO(inputEmail);

        // when
        ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/login") // POST 요청으로 변경
                .contentType(APPLICATION_JSON)
                .content(toJson(oauthRequestDTO)) // JSON 형식으로 요청 본문 설정
        );

        // then
        result.andExpectAll(
            status().isBadRequest(), // 상태 코드 확인
            content().contentType(APPLICATION_JSON), // 응답 콘텐츠 타입 확인
            content().json(toJson(INVALID_EMAIL_FORMAT)) // 오류 응답 확인
        ).andDo(restDocs.document(
            requestFields(
                fieldWithPath("email").description("잘못된 이메일 형식") // 요청 필드 문서화
            ),
            responseFields(
                fieldWithPath("code").description("오류 코드"), // 응답 필드 문서화
                fieldWithPath("message").description("오류에 대한 설명")
            )
        ));

        verify(oauthService, never()).processOauth(inputEmail); // 서비스 메서드 호출 검증
    }

}
