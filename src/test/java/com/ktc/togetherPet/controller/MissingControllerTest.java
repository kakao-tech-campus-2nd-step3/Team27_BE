package com.ktc.togetherPet.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ktc.togetherPet.annotation.OauthUserArgumentResolver;
import com.ktc.togetherPet.model.dto.missing.MissingPetRequestDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.service.MissingService;
import com.ktc.togetherPet.testConfig.RestDocsTestSupport;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("[같이 찾기] registerMissingPet 테스트")
    class registerMissingPetTest {

        private final OauthUserDTO oauthUserDTO = new OauthUserDTO("testEmail");
        private final String token = "testToken";

        @BeforeEach
        void setUp() {
            when(oauthUserArgumentResolver.supportsParameter(any())).thenReturn(true);
            when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(oauthUserDTO);
        }

        @Test
        @DisplayName("성공")
        void 성공() throws Exception {
            //given
            String token = "testToken";
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

            //when
            ResultActions actual = mockMvc.perform(
                post("/api/v0/missing")
                    .header("Authorization", token)
                    .content(toJson(missingPetRequestDTO))
                    .contentType(APPLICATION_JSON)
            );

            //then
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

    }

//    @Nested
//    @DisplayName("[같이 찾기] registerMissingPet 테스트")
//    class registerMissingPet {
//
//        //given
//        private OauthUserDTO oauthUserDTO;
//        private MissingPetRequestDTO missingPetDTO;
//
//        @BeforeEach
//        void setUp() throws Exception {
//            //given
//            oauthUserDTO = new OauthUserDTO("test@email.com");
//            missingPetDTO = new MissingPetRequestDTO(
//                "testPetName",
//                "testPetGender",
//                1L,
//                "testPetSpecies",
//                LocalDateTime.of(2024, 9, 25, 13, 19, 33),
//                35.17F,
//                126.90F,
//                "testPetFeature",
//                true
//            );
//
//            //when
//            when(oauthUserArgumentResolver.supportsParameter(any()))
//                .thenReturn(true);
//
//            when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
//                .thenReturn(oauthUserDTO);
//        }
//
//        private static Snippet authorizationSnippet() {
//            return HeaderDocumentation.requestHeaders(
//                headerWithName("Authorization").description("인증을 위한 Bearer 토큰")
//            );
//        }
//
//        private static Snippet petDTORequestSnippet() {
//            return PayloadDocumentation.requestFields(
//                fieldWithPath("pet_name").description("실종 동물의 이름"),
//                fieldWithPath("pet_gender").description("실종 동물의 성별"),
//                fieldWithPath("birth_month").description("실종 동물의 개월수"),
//                fieldWithPath("pet_breed").description("실종 동물의 종"),
//                fieldWithPath("lost_time").description("실종 시각"),
//                fieldWithPath("latitude").description("실종 위도"),
//                fieldWithPath("longitude").description("실종 경도"),
//                fieldWithPath("description").description("실종 동물의 특징"),
//                fieldWithPath("is_neutering").description("중성화 여부")
//            );
//        }
//
//        @Test
//        @DisplayName("registerMissingPet 201 Created")
//        void registerMissingPet_201() throws Exception {
//            // then
//            mockMvc.perform(
//                    post("/api/missing")
//                        .contentType(APPLICATION_JSON)
//                        .header("Authorization", "Bearer testTokens")
//                        .content(toJson(missingPetDTO))
//                )
//                .andExpect(status().isCreated())
//                .andDo(restDocs.document(
//                        authorizationSnippet(),
//                        petDTORequestSnippet()
//                    )
//                );
//
//            verify(missingService, times(1))
//                .registerMissingPet(oauthUserDTO, missingPetDTO);
//        }
//
//        @Nested
//        @DisplayName("registerMissingPet 400 Bad Request")
//        class registerMissingPet_400 {
//
//            @ParameterizedTest
//            @DisplayName("Age에 0이하의 값이 입력된 경우")
//            @ValueSource(ints = {-100, -10, 0})
//            void under0Age(int petBirthMonth) throws Exception {
//                //given
//                missingPetDTO = new MissingPetRequestDTO(
//                    "testPetName",
//                    "testPetGender",
//                    petBirthMonth, // 애완동물의 개월수에 따른 오류 발생 체크
//                    "testPetSpecies",
//                    LocalDateTime.of(2024, 9, 25, 13, 19, 33),
//                    35.17F,
//                    126.90F,
//                    "testPetFeature",
//                    true
//                );
//
//                // when
//                doThrow(invalidPetBirthMonthException())
//                    .when(missingService).registerMissingPet(oauthUserDTO, missingPetDTO);
//
//                // then
//                mockMvc.perform(
//                        post("/api/missing")
//                            .contentType(APPLICATION_JSON)
//                            .header("Authorization", "Bearer testTokens")
//                            .content(toJson(missingPetDTO))
//                    )
//                    .andExpectAll(
//                        status().isBadRequest(),
//                        content().contentType(APPLICATION_JSON),
//                        content().json(toJson(new ErrorResponse(INVALID_PET_MONTH)))
//                    )
//                    .andDo(restDocs.document(
//                        authorizationSnippet(),
//                        petDTORequestSnippet()
//                    ));
//
//                verify(missingService, times(1))
//                    .registerMissingPet(oauthUserDTO, missingPetDTO);
//            }
//
//            @ParameterizedTest
//            @DisplayName("날짜가 현재보다 늦은 경우")
//            @MethodSource("lateMoreThanNowMethodSource")
//            void lateMoreThanNow(LocalDateTime lostTime) throws Exception {
//                //given
//                missingPetDTO = new MissingPetRequestDTO(
//                    "testPetName",
//                    "testPetGender",
//                    1L,
//                    "testPetSpecies",
//                    lostTime,
//                    35.17F,
//                    126.90F,
//                    "testPetFeature",
//                    true
//                );
//
//                // when
//                doThrow(invalidDateException())
//                    .when(missingService).registerMissingPet(oauthUserDTO, missingPetDTO);
//
//                // then
//                mockMvc.perform(
//                        post("/api/missing")
//                            .contentType(APPLICATION_JSON)
//                            .header("Authorization", "Bearer testTokens")
//                            .content(toJson(missingPetDTO))
//                    )
//                    .andExpectAll(
//                        status().isBadRequest(),
//                        content().contentType(APPLICATION_JSON),
//                        content().json(toJson(new ErrorResponse(INVALID_DATE)))
//                    )
//                    .andDo(restDocs.document(
//                            authorizationSnippet(),
//                            petDTORequestSnippet()
//                        )
//                    );
//
//                verify(missingService, times(1))
//                    .registerMissingPet(oauthUserDTO, missingPetDTO);
//            }
//
//            private static Stream<LocalDateTime> lateMoreThanNowMethodSource() {
//                return Stream.of(
//                    LocalDateTime.now().plusHours(1L).truncatedTo(SECONDS),
//                    LocalDateTime.now().plusMinutes(10L).truncatedTo(SECONDS)
//                );
//            }
//
//            @ParameterizedTest
//            @DisplayName("위도 및 경도의 범위가 올바르지 않은 경우")
//            @MethodSource("incorrectRangeOfLatitudeAndLongitudeMethodSource")
//            void incorrectRangeOfLatitudeAndLongitude(float lostLatitude, float lostLongitude)
//                throws Exception {
//                // 위도는 -90 ~ 90도 사이
//                // 경도는 -180 ~ 180도 사이의 범위를 가져야만 한다.
//                //given
//                missingPetDTO = new MissingPetRequestDTO(
//                    "testPetName",
//                    "testPetGender",
//                    1L,
//                    "testPetSpecies",
//                    LocalDateTime.of(2024, 9, 25, 13, 19, 33),
//                    lostLatitude,
//                    lostLongitude,
//                    "testPetFeature",
//                    true
//                );
//
//                // when
//                doThrow(invalidLocationException())
//                    .when(missingService)
//                    .registerMissingPet(oauthUserDTO, missingPetDTO);
//
//                // then
//                mockMvc.perform(
//                        post("/api/missing")
//                            .contentType(APPLICATION_JSON)
//                            .header("Authorization", "Bearer testTokens")
//                            .content(toJson(missingPetDTO))
//                    )
//                    .andExpectAll(
//                        status().isBadRequest(),
//                        content().contentType(APPLICATION_JSON),
//                        content().json(toJson(new ErrorResponse(INVALID_LOCATION)))
//                    )
//                    .andDo(restDocs.document(
//                            authorizationSnippet(),
//                            petDTORequestSnippet()
//                        )
//                    );
//
//                verify(missingService, times(1))
//                    .registerMissingPet(oauthUserDTO, missingPetDTO);
//            }
//
//            private static Stream<Arguments> incorrectRangeOfLatitudeAndLongitudeMethodSource() {
//                return Stream.of(
//                    Arguments.of(-91, 0),
//                    Arguments.of(91, 0),
//                    Arguments.of(0, -181),
//                    Arguments.of(0, 181),
//                    Arguments.of(-91, 181),
//                    Arguments.of(-90.00001F, 0)
//                );
//            }
//        }
//
//        @Nested
//        @DisplayName("registerMissingPet 401 Unauthorized")
//        @DirtiesContext(classMode = BEFORE_CLASS)
//        class registerMissingPet_401 {
//
//            @Test
//            @DisplayName("not include Bearer")
//            public void notIncludeBearer() throws Exception {
//                // when
//                when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
//                    .thenThrow(invalidTokenException());
//
//                // then
//                mockMvc.perform(
//                        post("/api/missing")
//                            .contentType(APPLICATION_JSON)
//                            .header("Authorization", "testTokens")
//                            .content(toJson(missingPetDTO))
//                    )
//                    .andExpectAll(
//                        status().isUnauthorized(),
//                        content().contentType(APPLICATION_JSON),
//                        content().json(toJson(new ErrorResponse(tokenInvalid)))
//                    )
//                    .andDo(restDocs.document(
//                            authorizationSnippet(),
//                            petDTORequestSnippet()
//                        )
//                    );
//
//                verify(oauthUserArgumentResolver, times(1))
//                    .resolveArgument(any(), any(), any(), any());
//
//                verify(missingService, never())
//                    .registerMissingPet(oauthUserDTO, missingPetDTO);
//            }
//
//            @Test
//            @DisplayName("expired Token")
//            public void expiredToken() throws Exception {
//                // when
//                when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
//                    .thenThrow(expiredTokenException());
//
//                // then
//                mockMvc.perform(
//                        post("/api/missing")
//                            .contentType(APPLICATION_JSON)
//                            .header("Authorization", "bearer testTokens")
//                            .content(toJson(missingPetDTO))
//                    )
//                    .andExpectAll(
//                        status().isUnauthorized(),
//                        content().contentType(APPLICATION_JSON),
//                        content().json(toJson(new ErrorResponse(EXPIRED_TOKEN)))
//                    ).andDo(restDocs.document(
//                            authorizationSnippet(),
//                            petDTORequestSnippet()
//                        )
//                    );
//
//                verify(oauthUserArgumentResolver, times(1))
//                    .resolveArgument(any(), any(), any(), any());
//
//                verify(missingService, never())
//                    .registerMissingPet(oauthUserDTO, missingPetDTO);
//            }
//
//            @Test
//            @DisplayName("invalid user")
//            public void invalidUser() throws Exception {
//                // when
//                when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
//                    .thenThrow(invalidUserException());
//
//                // then
//                mockMvc.perform(
//                        post("/api/missing")
//                            .contentType(APPLICATION_JSON)
//                            .header("Authorization", "Bearer testTokens")
//                            .content(toJson(missingPetDTO))
//                    )
//                    .andExpectAll(
//                        status().isUnauthorized(),
//                        content().contentType(APPLICATION_JSON),
//                        content().json(toJson(new ErrorResponse(INVALID_USER)))
//                    ).andDo(restDocs.document(
//                            authorizationSnippet(),
//                            petDTORequestSnippet()
//                        )
//                    );
//
//                verify(oauthUserArgumentResolver, times(1))
//                    .resolveArgument(any(), any(), any(), any());
//
//                verify(missingService, never())
//                    .registerMissingPet(oauthUserDTO, missingPetDTO);
//            }
//
//        }
//
//        @Test
//        @DisplayName("registerMissingPet 404 Not Found")
//        void registerMissingPet_404() throws Exception {
//            // when
//            doThrow(breedNotFoundException())
//                .when(missingService)
//                .registerMissingPet(oauthUserDTO, missingPetDTO);
//
//            // then
//            mockMvc.perform(
//                    post("/api/missing")
//                        .contentType(APPLICATION_JSON)
//                        .header("Authorization", "Bearer testTokens")
//                        .content(toJson(missingPetDTO))
//                )
//                .andExpectAll(
//                    status().isNotFound(),
//                    content().contentType(APPLICATION_JSON),
//                    content().json(toJson(new ErrorResponse(BREED_NOT_FOUND)))
//                )
//                .andDo(restDocs.document(
//                    authorizationSnippet(),
//                    petDTORequestSnippet()
//                ));
//
//            verify(missingService, times(1))
//                .registerMissingPet(oauthUserDTO, missingPetDTO);
//        }
//    }
//
//    @Nested
//    @DisplayName("[같이 찾기] getMissingPetsNearByRegion 테스트")
//    class getMissingPetsNearByRegion {
//
//        private static Snippet petDTORequestSnippet() {
//            return RequestDocumentation.queryParameters(
//                parameterWithName("latitude").description("가져오고자 하는 위치의 위도"),
//                parameterWithName("longitude").description("가져오고자 하는 위치의 경도")
//            );
//        }
//
//        @Test
//        @DisplayName("getMissingPetsNearByRegion 200 OK")
//        void getMissingPetsNearByRegion_200() throws Exception {
//            //given
//            float latitude = 37.5F;
//            float longitude = 80.1F;
//
//            List<MissingPetNearByResponseDTO> expects = List.of(
//                new MissingPetNearByResponseDTO(
//                    1L,
//                    32.5F,
//                    80.0F,
//                    "test-pet-image-url-1"
//                ),
//                new MissingPetNearByResponseDTO(
//                    2L,
//                    33.5F,
//                    81.0F,
//                    "test-pet-image-url-2"
//                ),
//                new MissingPetNearByResponseDTO(
//                    3L,
//                    34.5F,
//                    82.0F,
//                    "test-pet-image-url-3"
//                )
//            );
//
//            //when
//            when(missingService.getMissingPetsNearBy(latitude, longitude))
//                .thenReturn(expects);
//
//            //then
//            mockMvc.perform(
//                get("/api/missing")
//                    .contentType(APPLICATION_JSON)
//                    .param("latitude", String.valueOf(latitude))
//                    .param("longitude", String.valueOf(longitude))
//            ).andExpectAll(
//                status().isOk(),
//                content().json(toJson(expects))
//            ).andDo(restDocs.document(
//                petDTORequestSnippet(),
//                responseFields(
//                    fieldWithPath("[].pet_id").description("해당 애완동물의 id"),
//                    fieldWithPath("[].latitude").description("실종 위도"),
//                    fieldWithPath("[].longitude").description("실종 경도"),
//                    fieldWithPath("[].pet_image_url").description("애완동물의 사진 url")
//                )
//            ));
//
//            verify(missingService, times(1))
//                .getMissingPetsNearBy(latitude, longitude);
//        }
//
//        @Test
//        @DisplayName("getMissingPetsNearByRegion 400 BadRequest")
//        void getMissingPetsNearByRegion_400() throws Exception {
//            //given
//            float latitude = 37.5F;
//            float longitude = 80.1F;
//
//            //when
//            when(missingService.getMissingPetsNearBy(latitude, longitude))
//                .thenThrow(invalidLocationException());
//
//            //then
//            mockMvc.perform(
//                get("/api/missing")
//                    .contentType(APPLICATION_JSON)
//                    .param("latitude", String.valueOf(latitude))
//                    .param("longitude", String.valueOf(longitude))
//            ).andExpectAll(
//                status().isBadRequest(),
//                content().json(toJson(new ErrorResponse(INVALID_LOCATION)))
//            ).andDo(restDocs.document(
//                    petDTORequestSnippet(),
//                    responseFields(
//                        fieldWithPath("message").description("에러 메시지")
//                    )
//                )
//            );
//
//            verify(missingService, times(1))
//                .getMissingPetsNearBy(latitude, longitude);
//        }
//    }
//
//    @Nested
//    @DisplayName("[같이 찾기] getMissingPetDetailByMissingId 테스트")
//    class getMissingPetDetailByMissingId {
//
//        private static Snippet requestPathVariableSnippet() {
//            return RequestDocumentation.pathParameters(
//                parameterWithName("missing-id").description("실종 아이디")
//            );
//        }
//
//        private long missingId = 1L;
//
//        private MissingPetDetailResponseDTO missingPetDetailDTO = new MissingPetDetailResponseDTO(
//            "test-name",
//            "test-breed",
//            1L,
//            10.0F,
//            11.0F,
//            "test-description",
//            List.of("image-url-1", "image-url-2")
//        );
//
//
//        @Test
//        @DisplayName("getMissingPetDetailByMissingId")
//        void getMissingPetDetailByMissingId_200() throws Exception {
//            // when
//            when(missingService.getMissingPetDetailByMissingId(missingId))
//                .thenReturn(missingPetDetailDTO);
//
//            // then
//            mockMvc.perform(
//                get("/api/missing/{missing-id}", missingId)
//                    .contentType(APPLICATION_JSON)
//            ).andExpectAll(
//                status().isOk(),
//                content().json(toJson(missingPetDetailDTO))
//            ).andDo(restDocs.document(
//                requestPathVariableSnippet(),
//                responseFields(
//                    fieldWithPath("name").description("실종 애완동물의 이름"),
//                    fieldWithPath("breed").description("실종 애완동물의 종"),
//                    fieldWithPath("birth_month").description("실종 애완동물의 개월수"),
//                    fieldWithPath("latitude").description("실종 위도"),
//                    fieldWithPath("longitude").description("실종 경도"),
//                    fieldWithPath("description").description("실종 애완동물의 특징"),
//                    fieldWithPath("image_url").description("실종 애완동물의 이미지 url")
//                )
//            ));
//
//            verify(missingService, times(1))
//                .getMissingPetDetailByMissingId(missingId);
//        }
//
//        @Test
//        @DisplayName("getMissingPetDetailByMissingId_404")
//        void getMissingPetDetailByMissingId_404() throws Exception {
//            // when
//            when(missingService.getMissingPetDetailByMissingId(missingId))
//                .thenThrow(missingNotFound());
//
//            // then
//            mockMvc.perform(
//                get("/api/missing/{missing-id}", missingId)
//            ).andExpectAll(
//                status().isNotFound(),
//                content().json(toJson(new ErrorResponse(MISSING_NOT_FOUND)))
//            ).andDo(restDocs.document(
//                requestPathVariableSnippet(),
//                responseFields(
//                    fieldWithPath("message").description("에러 메시지")
//                )
//            ));
//
//            verify(missingService, times(1))
//                .getMissingPetDetailByMissingId(missingId);
//        }
//    }
}