package com.ktc.togetherPet.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ktc.togetherPet.annotation.OauthUserArgumentResolver;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.report.ReportCreateRequestDTO;
import com.ktc.togetherPet.model.dto.report.ReportDetailResponseDTO;
import com.ktc.togetherPet.model.dto.report.ReportNearByResponseDTO;
import com.ktc.togetherPet.model.dto.report.ReportResponseDTO;
import com.ktc.togetherPet.service.ReportService;
import com.ktc.togetherPet.testConfig.RestDocsTestSupport;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ReportController.class)
class ReportControllerTest extends RestDocsTestSupport {

    @MockBean
    private ReportService reportService;

    @MockBean
    private OauthUserArgumentResolver oauthUserArgumentResolver;

    @Test
    @DisplayName("제보 등록 테스트(임의 제보)/createReport")
    void 제보_등록_임의제보() throws Exception {
        // given
        ReportCreateRequestDTO reportCreateRequestDTO = new ReportCreateRequestDTO(
            "testColor",
            15.0D,
            15.0D,
            LocalDateTime.of(2024, 10, 9, 17, 53, 11),
            "testDescription",
            "testBreed",
            "testGender",
            null
        );

        MockMultipartFile reportCreateRequestDTOMock = new MockMultipartFile(
            "reportCreateRequestDTO",
            "reportCreateRequestDTO",
            APPLICATION_JSON_VALUE,
            toJson(reportCreateRequestDTO).getBytes()
        );

        MockMultipartFile image1 = new MockMultipartFile(
            "files",
            "image1.jpeg",
            IMAGE_JPEG_VALUE,
            "image1".getBytes()
        );
        MockMultipartFile image2 = new MockMultipartFile(
            "files",
            "image2.jpeg",
            IMAGE_JPEG_VALUE,
            "image2".getBytes()

        );

        String token = "Bearer testToken";
        OauthUserDTO oauthUserDTO = new OauthUserDTO("test@email.com");

        // when
        when(oauthUserArgumentResolver.supportsParameter(any()))
            .thenReturn(true);
        when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
            .thenReturn(oauthUserDTO);

        ResultActions result = mockMvc.perform(
            multipart("/api/v0/report")
                .file(reportCreateRequestDTOMock)
                .file(image1)
                .file(image2)
                .header("Authorization", token)
        );

        // then
        result.andExpect(status().isCreated())
            .andDo(restDocs.document(
                requestParts(
                    partWithName("reportCreateRequestDTO").description("제보를 위한 정보"),
                    partWithName("files").description("제보하고자 하는 동물의 이미지")
                ),
                requestPartFields(
                    "reportCreateRequestDTO",
                    fieldWithPath("color").description("제보하고자 하는 동물의 색상"),
                    fieldWithPath("found_latitude").description("제보하고자 하는 위치의 위도"),
                    fieldWithPath("found_longitude").description("제보하고자 하는 위치의 경도"),
                    fieldWithPath("found_date").description("제보하고자 하는 시각"),
                    fieldWithPath("description").description("제보 설명"),
                    fieldWithPath("breed").description("제보하고자 하는 동물의 종"),
                    fieldWithPath("gender").description("제보하고자 하는 동물의 성별")
                )
            ));

        verify(reportService, times(1))
            .createReport(reportCreateRequestDTO, List.of(image1, image2), oauthUserDTO);

    }

    @Test
    @DisplayName("제보 등록 테스트(실종 등록된 제보)/createReport")
    void 제보_등록_실종등록() throws Exception {
        // given
        ReportCreateRequestDTO reportCreateRequestDTO = new ReportCreateRequestDTO(
            "testColor",
            15.0D,
            15.0D,
            LocalDateTime.of(2024, 10, 9, 17, 53, 11),
            "testDescription",
            "testBreed",
            "testGender",
            1L
        );

        MockMultipartFile reportCreateRequestDTOMock = new MockMultipartFile(
            "reportCreateRequestDTO",
            "reportCreateRequestDTO",
            APPLICATION_JSON_VALUE,
            toJson(reportCreateRequestDTO).getBytes()
        );

        MockMultipartFile image1 = new MockMultipartFile(
            "files",
            "image1.jpeg",
            IMAGE_JPEG_VALUE,
            "image1".getBytes()
        );
        MockMultipartFile image2 = new MockMultipartFile(
            "files",
            "image2.jpeg",
            IMAGE_JPEG_VALUE,
            "image2".getBytes()

        );

        String token = "Bearer testToken";
        OauthUserDTO oauthUserDTO = new OauthUserDTO("test@email.com");

        // when
        when(oauthUserArgumentResolver.supportsParameter(any()))
            .thenReturn(true);
        when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
            .thenReturn(oauthUserDTO);

        ResultActions result = mockMvc.perform(
            multipart("/api/v0/report")
                .file(reportCreateRequestDTOMock)
                .file(image1)
                .file(image2)
                .header("Authorization", token)
        );

        // then
        result.andExpect(status().isCreated())
            .andDo(restDocs.document(
                requestParts(
                    partWithName("reportCreateRequestDTO").description("제보를 위한 정보"),
                    partWithName("files").description("제보하고자 하는 동물의 이미지")
                ),
                requestPartFields(
                    "reportCreateRequestDTO",
                    fieldWithPath("color").description("제보하고자 하는 동물의 색상"),
                    fieldWithPath("found_latitude").description("제보하고자 하는 위치의 위도"),
                    fieldWithPath("found_longitude").description("제보하고자 하는 위치의 경도"),
                    fieldWithPath("found_date").description("제보하고자 하는 시각"),
                    fieldWithPath("description").description("제보 설명"),
                    fieldWithPath("breed").description("제보하고자 하는 동물의 종"),
                    fieldWithPath("gender").description("제보하고자 하는 동물의 성별"),
                    fieldWithPath("missing_id").description("제보하고자 하는 실종에 대한 id")
                )
            ));

        verify(reportService, times(1))
            .createReport(reportCreateRequestDTO, List.of(image1, image2), oauthUserDTO);

    }

    @Test
    @DisplayName("받은 제보 확인 테스트/getReceivedReports")
    void 받은_제보_확인() throws Exception {
        // given
        String token = "Bearer testToken";
        OauthUserDTO oauthUserDTO = new OauthUserDTO("test@email.com");
        List<ReportResponseDTO> actual = List.of(
            new ReportResponseDTO(
                1L,
                15.0D,
                15.0D,
                "https://together-pet/api/v0/images/test-image-1"
            ),
            new ReportResponseDTO(
                2L,
                30.0D,
                25.0D,
                "https://together-pet/api/v0/images/test-image-2"
            )
        );

        // when
        when(oauthUserArgumentResolver.supportsParameter(any()))
            .thenReturn(true);

        when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
            .thenReturn(oauthUserDTO);

        when(reportService.getReceivedReports(oauthUserDTO))
            .thenReturn(actual);

        ResultActions result = mockMvc.perform(
            get("/api/v0/report/user")
                .contentType(APPLICATION_JSON_VALUE)
                .header("Authorization", token)
        );

        // then
        result.andExpectAll(
            status().isOk(),
            content().contentType(APPLICATION_JSON),
            content().json(toJson(actual))
        ).andDo(restDocs.document(
            requestHeaders(
                headerWithName("Authorization").description("인증을 위한 Bearer 토큰")
            ),
            responseFields(
                fieldWithPath("[].id").description("제보 id"),
                fieldWithPath("[].latitude").description("제보 위도"),
                fieldWithPath("[].longitude").description("제보 경도"),
                fieldWithPath("[].image_url").description("대표 이미지 경로")
            )
        ));

        verify(reportService, times(1))
            .getReceivedReports(oauthUserDTO);
    }

    @Test
    @DisplayName("지역의 실종 제보 확인/getNearByReports")
    void 지역의_실종_제보_확인() throws Exception {
        // given
        double latitude = 15.0D;
        double longitude = 30.0D;

        List<ReportNearByResponseDTO> actual = List.of(
            new ReportNearByResponseDTO(
                1L,
                16.0D,
                31.0D,
                "https://together-pet/api/v0/images/test-image-1"
            ),
            new ReportNearByResponseDTO(
                2L,
                17.0D,
                32.0D,
                "https://together-pet/api/v0/images/test-image-2"
            )
        );

        // when
        when(reportService.getReportsByLocation(latitude, longitude))
            .thenReturn(actual);

        ResultActions result = mockMvc.perform(
            get("/api/v0/report/location")
                .queryParam("latitude", String.valueOf(latitude))
                .queryParam("longitude", String.valueOf(longitude))
        );

        // then
        result.andExpectAll(
            status().isOk(),
            content().contentType(APPLICATION_JSON),
            content().json(toJson(actual))
        );

        verify(reportService, times(1))
            .getReportsByLocation(latitude, longitude);
    }

    @Test
    @DisplayName("제보 상세 정보 확인 테스트/getReportDetail")
    void 제보_상세_정보_확인() throws Exception {
        // given
        long reportId = 1L;

        ReportDetailResponseDTO actual = new ReportDetailResponseDTO(
            "testPetBreed",
            "testPetColor",
            "testPetGender",
            15.0F,
            16.0F,
            "testDescription",
            "testReporterName",
            List.of(
                "https://together-pet/api/v0/images/test-image-url-1",
                "https://together-pet/api/v0/images/test-image-url-2"
            ),
            LocalDateTime.of(2024, 10, 9, 18, 37, 22)
        );

        // when
        when(reportService.getReportById(reportId))
            .thenReturn(actual);

        ResultActions result = mockMvc.perform(
            get("/api/v0/report/{report-id}", reportId)
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andExpectAll(
            status().isOk(),
            content().contentType(APPLICATION_JSON),
            content().json(toJson(actual))
        ).andDo(restDocs.document(
            pathParameters(
                parameterWithName("report-id").description("확인하고자 하는 제보의 id")
            ),
            responseFields(
                fieldWithPath("pet_breed").description("애완동물의 종"),
                fieldWithPath("pet_color").description("애완동물의 색상"),
                fieldWithPath("pet_gender").description("애완동물의 성별"),
                fieldWithPath("latitude").description("제보된 위치의 위도"),
                fieldWithPath("longitude").description("제보된 위치의 경도"),
                fieldWithPath("description").description("제보에 대한 설명"),
                fieldWithPath("reporter_name").description("제보자 이름"),
                fieldWithPath("image_url[]").description("제보 사진 리스트"),
                fieldWithPath("found_date").description("제보된 시각")
            )
        ));

        verify(reportService, times(1))
            .getReportById(reportId);
    }
}