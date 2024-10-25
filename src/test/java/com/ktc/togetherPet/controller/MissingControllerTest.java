package com.ktc.togetherPet.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ktc.togetherPet.annotation.OauthUserArgumentResolver;
import com.ktc.togetherPet.model.dto.missing.MissingPetDetailResponseDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetNearByResponseDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetRequestDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.service.MissingService;
import com.ktc.togetherPet.testConfig.RestDocsTestSupport;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MissingController.class)
class MissingControllerTest extends RestDocsTestSupport {

    @MockBean
    private MissingService missingService;

    @MockBean
    private OauthUserArgumentResolver oauthUserArgumentResolver;

    @Test
    @DisplayName("실종등록 테스트/registerMissingPet")
    void 실종등록() throws Exception {
        // given
        String token = "testToken";
        OauthUserDTO oauthUserDTO = new OauthUserDTO("testEmail");

        MissingPetRequestDTO missingPetRequestDTO = new MissingPetRequestDTO(
            "testPetName",
            "male",
            1L,
            "testPetBreed",
            LocalDateTime.of(2024, 10, 9, 14, 32, 22),
            15.5F,
            15.5F,
            "testDescription",
            true
        );

        // when
        when(oauthUserArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
            .thenReturn(oauthUserDTO);

        ResultActions actual = mockMvc.perform(
            post("/api/v1/missing")
                .header("Authorization", token)
                .content(toJson(missingPetRequestDTO))
                .contentType(APPLICATION_JSON)
        );

        // then
        actual
            .andExpect(status().isCreated())
            .andDo(restDocs.document(
                HeaderDocumentation.requestHeaders(
                    headerWithName("Authorization").description("인증을 위한 Bearer 토큰")
                ),
                PayloadDocumentation.requestFields(
                    fieldWithPath("pet_name").description("실종 동물의 이름"),
                    fieldWithPath("pet_gender").description("실종 동물의 성별"),
                    fieldWithPath("birth_month").description("실종 동물의 개월수"),
                    fieldWithPath("pet_breed").description("실종 동물의 종"),
                    fieldWithPath("lost_time").description("실종 시각"),
                    fieldWithPath("latitude").description("실종 위도"),
                    fieldWithPath("longitude").description("실종 경도"),
                    fieldWithPath("description").description("실종 동물의 특징"),
                    fieldWithPath("is_neutering").description("중성화 여부")
                )
            ));

        verify(missingService, times(1))
            .registerMissingPet(oauthUserDTO, missingPetRequestDTO);
    }

    @Test
    @DisplayName("근처 실종 정보 가져오기 테스트/getMissingPetsNearByRegion")
    void 근처_실종_정보_가져오기() throws Exception {
        // given
        double latitude = 15.0D;
        double longitude = 15.0D;

        List<MissingPetNearByResponseDTO> actual = List.of(
            new MissingPetNearByResponseDTO(
                1L,
                1L,
                14.0D,
                15.0D,
                "testPetImageUrl1"
            ),
            new MissingPetNearByResponseDTO(
                2L,
                2L,
                14.0D,
                15.0D,
                "testPetImageUrl1"
            )
        );

        // when
        when(missingService.getMissingPetsNearBy(latitude, longitude))
            .thenReturn(actual);

        ResultActions result = mockMvc.perform(
            get("/api/v1/missing")
                .contentType(APPLICATION_JSON)
                .queryParam("latitude", String.valueOf(latitude))
                .queryParam("longitude", String.valueOf(longitude))
        );

        // then
        result.andExpectAll(
            status().isOk(),
            content().contentType(APPLICATION_JSON),
            content().json(toJson(actual))
        ).andDo(restDocs.document(
            queryParameters(
                parameterWithName("latitude").description("가져오고자 하는 위치의 위도"),
                parameterWithName("longitude").description("가져오고자 하는 위치의 경도")
            ),
            responseFields(
                fieldWithPath("[].missing_id").description("실종 정보 id"),
                fieldWithPath("[].pet_id").description("해당 애완동물의 id"),
                fieldWithPath("[].latitude").description("실종 위도"),
                fieldWithPath("[].longitude").description("실종 경도"),
                fieldWithPath("[].pet_image_url").description("애완동물의 사진 url")
            )
        ));

        verify(missingService, times(1))
            .getMissingPetsNearBy(latitude, longitude);

    }

    @Test
    @DisplayName("자세한 실종 정보 가져오기 테스트/getMissingPetDetailByMissingId")
    void 자세한_실종_정보_가져오기() throws Exception {
        // given
        long missingId = 1L;
        MissingPetDetailResponseDTO actual = new MissingPetDetailResponseDTO(
            "testName",
            "testBreed",
            1L,
            15.0D,
            15.0D,
            "testDescription",
            List.of("testImageUrl-1", "testImageUrl-2")
        );

        // when
        when(missingService.getMissingPetDetailByMissingId(missingId))
            .thenReturn(actual);

        ResultActions result = mockMvc.perform(
            get("/api/v1/missing/{missing-id}", missingId)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andExpectAll(
            status().isOk(),
            content().contentType(APPLICATION_JSON),
            content().json(toJson(actual))
        ).andDo(restDocs.document(
            pathParameters(
                parameterWithName("missing-id").description("실종 아이디")
            ),
            responseFields(
                fieldWithPath("name").description("실종 애완동물의 이름"),
                fieldWithPath("breed").description("실종 애완동물의 종"),
                fieldWithPath("birth_month").description("실종 애완동물의 개월수"),
                fieldWithPath("latitude").description("실종 위도"),
                fieldWithPath("longitude").description("실종 경도"),
                fieldWithPath("description").description("실종 애완동물의 특징"),
                fieldWithPath("image_url").description("실종 애완동물의 이미지 url")
            )
        ));

        verify(missingService, times(1))
            .getMissingPetDetailByMissingId(missingId);
    }
}