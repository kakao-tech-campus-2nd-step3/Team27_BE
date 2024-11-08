package com.ktc.togetherPet.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.walk.CalorieResponseDTO;
import com.ktc.togetherPet.model.dto.walk.LocationDTO;
import com.ktc.togetherPet.model.dto.walk.WalkPathByDateResponseDTO;
import com.ktc.togetherPet.model.dto.walk.WalkRequestDTO;
import com.ktc.togetherPet.model.dto.walk.WalkResponseDTO;
import com.ktc.togetherPet.service.WalkService;
import com.ktc.togetherPet.testConfig.RestDocsTestSupport;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(WalkController.class)
class WalkControllerTest extends RestDocsTestSupport {

    @MockBean
    private WalkService walkService;

    private String token;
    private OauthUserDTO oauthUserDTO;

    @BeforeEach
    void tokenSetUp() {
        token = "Bearer testTOKEN";
        oauthUserDTO = new OauthUserDTO("1");

        when(oauthUserArgumentResolver.supportsParameter(any()))
            .thenReturn(true);

        when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
            .thenReturn(oauthUserDTO);
    }

    @Test
    @DisplayName("산책 등록 테스트/createWalk")
    void 산책_정보_등록() throws Exception {
        // given
        WalkRequestDTO walkRequestDTO = new WalkRequestDTO(
            1.5F,
            100L,
            List.of(
                new LocationDTO(1.1F, 1.1F),
                new LocationDTO(1.2F, 1.2F),
                new LocationDTO(1.3F, 1.3F)
            )
        );

        CalorieResponseDTO actual = new CalorieResponseDTO(100L);

        // when
        when(walkService.createWalk(oauthUserDTO, walkRequestDTO))
            .thenReturn(actual);

        ResultActions result = mockMvc.perform(
            post("/api/v1/walk")
                .header("Authorization", token)
                .content(toJson(walkRequestDTO))
                .contentType(APPLICATION_JSON)
        );

        // then
        result.andExpectAll(
            status().isOk(),
            content().contentType(APPLICATION_JSON),
            content().json(toJson(actual))
        ).andDo(restDocs.document(
            requestHeaders(
                headerWithName("Authorization").description("Bearer를 포함한 JWT 토큰")
            ),
            requestFields(
                fieldWithPath("total_walk_distance").description("산책 거리"),
                fieldWithPath("total_walk_time").description("소모 시간"),
                fieldWithPath("location_list").description("산책 경로 좌표"),
                fieldWithPath("location_list[].latitude").description("산책 경로 중 한 지점의 위도"),
                fieldWithPath("location_list[].longitude").description("산책 경로 중 한 지점의 경도")
            ),
            responseFields(
                fieldWithPath("calorie").description("소모 칼로리")
            )
        ));

        verify(walkService, times(1))
            .createWalk(oauthUserDTO, walkRequestDTO);
    }

    @Test
    @DisplayName("산책 정보 가져오기 테스트/getWalkInformation")
    void 산책_정보_가져오기() throws Exception {
        // given
        WalkResponseDTO actual = new WalkResponseDTO(
            1,
            100L,
            50L,
            1000.0D,
            500.0D,
            10000L,
            5000L
        );

        // when
        when(walkService.getWalkInformation(oauthUserDTO))
            .thenReturn(actual);

        ResultActions result = mockMvc.perform(
            get("/api/v1/walk")
                .header("Authorization", token)
        );

        // then
        result.andExpectAll(
            status().isOk(),
            content().contentType(APPLICATION_JSON),
            content().json(toJson(actual))
        ).andDo(restDocs.document(
            requestHeaders(
                headerWithName("Authorization").description("Bearer를 포함한 JWT 토큰")
            ),
            responseFields(
                fieldWithPath("flag_value").description("산책 정보 플래그"),
                fieldWithPath("total_count").description("오늘 산책 횟수"),
                fieldWithPath("avg_walk_count").description("평균 산책 횟수"),
                fieldWithPath("total_walk_distance").description("오늘 총 산책 거리"),
                fieldWithPath("avg_walk_distance").description("평균 산책 거리"),
                fieldWithPath("total_walk_time").description("오늘 총 산책 시간"),
                fieldWithPath("avg_walk_time").description("평균 산책 시간")
            )
        ));

        verify(walkService, times(1))
            .getWalkInformation(oauthUserDTO);
    }

    @Test
    @DisplayName("날짜별 산책 경로 정보 가져오기 테스트/getWalkPathListByDate")
    void 날짜별_경로_및_정보_가져오기() throws Exception {
        // given
        LocalDateTime testDate = LocalDateTime.now();

        List<WalkPathByDateResponseDTO> actual = List.of(
            new WalkPathByDateResponseDTO(
                List.of(
                    new LocationDTO(1.1F, 1.1F),
                    new LocationDTO(1.2F, 1.2F),
                    new LocationDTO(1.3F, 1.3F)
                ),
                1.5F,
                100L,
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(10000 / 1000)
            ),
            new WalkPathByDateResponseDTO(
                List.of(
                    new LocationDTO(2.1F, 2.1F),
                    new LocationDTO(2.2F, 2.2F),
                    new LocationDTO(2.3F, 2.3F)
                ),
                1.5F,
                100L,
                LocalDateTime.now().plusSeconds(20000 / 1000),
                LocalDateTime.now().plusSeconds(40000 / 1000)
            )
        );

        // when
        when(walkService.getWalkPathByDate(oauthUserDTO, testDate))
            .thenReturn(actual);

        ResultActions result = mockMvc.perform(
            get("/api/v1/walk/paths")
                .header("Authorization", token)
                .queryParam("date", testDate.toString())
        );

        // then
        result.andExpectAll(
            status().isOk(),
            content().contentType(APPLICATION_JSON),
            content().json(toJson(actual))
        ).andDo(restDocs.document(
            requestHeaders(
                headerWithName("Authorization").description("Bearer를 포함한 JWT 토큰")
            ),
            responseFields(
                fieldWithPath("[].location_list").description("산책 경로 좌표"),
                fieldWithPath("[].location_list[].latitude").description("산책 경로 중 한 지점의 위도"),
                fieldWithPath("[].location_list[].longitude").description("산책 경로 중 한 지점의 경도"),
                fieldWithPath("[].walk_distance").description("산책 거리"),
                fieldWithPath("[].walk_time").description("산책 시간"),
                fieldWithPath("[].walk_start_time_point").description("산책 시작 시간"),
                fieldWithPath("[].walk_end_time_point").description("산책 종료 시간")
            )
        ));
    }
}