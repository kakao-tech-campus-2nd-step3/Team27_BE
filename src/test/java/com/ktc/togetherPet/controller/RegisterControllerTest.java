package com.ktc.togetherPet.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ktc.togetherPet.annotation.OauthUserArgumentResolver;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.pet.PetRegisterRequestDTO;
import com.ktc.togetherPet.service.RegisterService;
import com.ktc.togetherPet.testConfig.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(RegisterController.class)
class RegisterControllerTest extends RestDocsTestSupport {

    @MockBean
    private RegisterService registerService;

    @MockBean
    private OauthUserArgumentResolver oauthUserArgumentResolver;

    @Test
    @DisplayName("사용자와 펫 정보 등록 테스트/registerPetUser")
    void 사용자와_펫_정보_등록() throws Exception {
        // given
        PetRegisterRequestDTO petRegisterRequestDTO = new PetRegisterRequestDTO(
            "testPetName",
            1,
            "testPetType",
            true,
            "testPetFeature"
        );
        MockMultipartFile petRegisterRequestDTOMock = new MockMultipartFile(
            "PetRegisterDTO",
            "petRegisterDTO",
            APPLICATION_JSON_VALUE,
            toJson(petRegisterRequestDTO).getBytes()
        );

        MockMultipartFile petImageMock = new MockMultipartFile(
            "petImage",
            "testPetImage.jpeg",
            IMAGE_JPEG_VALUE,
            "이미지 파일 내용".getBytes()
        );

        String userName = "testUserName";

        MockMultipartFile userNameMock = new MockMultipartFile(
            "userName",
            "userName",
            TEXT_PLAIN_VALUE,
            userName.getBytes()
        );

        String token = "Bearer testToken";
        OauthUserDTO oauthUserDTO = new OauthUserDTO(
            "test@email.com"
        );

        // when
        when(oauthUserArgumentResolver.supportsParameter(any()))
            .thenReturn(true);

        when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
            .thenReturn(oauthUserDTO);

        ResultActions result = mockMvc.perform(
            multipart("/api/v1/register")
                .file(petRegisterRequestDTOMock)
                .file(petImageMock)
                .file(userNameMock)
                .header("Authorization", token)
        );

        // then
        result.andExpect(status().isCreated())
            .andDo(restDocs.document(
                requestHeaders(
                    headerWithName("Authorization").description("Bearer을 포함한 토큰")
                ),
                requestParts(
                    partWithName("PetRegisterDTO").description("펫 등록을 위한 정보"),
                    partWithName("petImage").description("펫의 프로필을 등록하기 위한 사진"),
                    partWithName("userName").description("사용자 이름")
                ),
                requestPartFields(
                    "PetRegisterDTO",
                    fieldWithPath("pet_name").description("애완 동물의 이름"),
                    fieldWithPath("pet_birth_month").description("애완 동물의 개월 수"),
                    fieldWithPath("pet_type").description("애완 동물의 종"),
                    fieldWithPath("is_neutering").description("애완 동물의 중성화 여부"),
                    fieldWithPath("pet_feature").description("애완 동물의 특징")
                )
            ));

        verify(registerService, times(1))
            .create(petRegisterRequestDTO, petImageMock, oauthUserDTO.email(), userName);
    }
}