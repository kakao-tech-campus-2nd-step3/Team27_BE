package com.ktc.togetherPet.controller;

import static com.ktc.togetherPet.exception.CustomException.breedNotFoundException;
import static com.ktc.togetherPet.exception.CustomException.expiredTokenException;
import static com.ktc.togetherPet.exception.CustomException.invalidDateException;
import static com.ktc.togetherPet.exception.CustomException.invalidLocaltionException;
import static com.ktc.togetherPet.exception.CustomException.invalidPetBirthMonthException;
import static com.ktc.togetherPet.exception.CustomException.invalidTokenException;
import static com.ktc.togetherPet.exception.CustomException.invalidUserException;
import static com.ktc.togetherPet.exception.ErrorMessage.BREED_NOT_FOUND;
import static com.ktc.togetherPet.exception.ErrorMessage.EXPIRED_TOKEN;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_DATE;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_LOCATION;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_PET_MONTH;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_USER;
import static com.ktc.togetherPet.exception.ErrorMessage.tokenInvalid;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ktc.togetherPet.annotation.OauthUserArgumentResolver;
import com.ktc.togetherPet.exception.ErrorResponse;
import com.ktc.togetherPet.model.dto.missing.MissingPetDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.vo.BirthMonthDTO;
import com.ktc.togetherPet.model.dto.vo.LocationDTO;
import com.ktc.togetherPet.service.MissingService;
import com.ktc.togetherPet.testConfig.RestDocsTestSupport;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@WebMvcTest(MissingController.class)
class MissingControllerTest extends RestDocsTestSupport {

    @MockBean
    private OauthUserArgumentResolver oauthUserArgumentResolver;

    @MockBean
    private MissingService missingService;

    //given
    private OauthUserDTO oauthUserDTO;
    private MissingPetDTO missingPetDTO;

    @BeforeEach
    void setUp() {
        oauthUserDTO = new OauthUserDTO("test@email.com");
        missingPetDTO = new MissingPetDTO(
            "testPetName",
            "testPetGender",
            new BirthMonthDTO(1),
            "testPetSpecies",
            LocalDateTime.of(2024, 9, 25, 13, 19, 33),
            new LocationDTO(35.17F, 126.90F),
            "testPetFeature",
            true
        );
    }

    private static Snippet authorizationSnippet() {
        return HeaderDocumentation.requestHeaders(
            headerWithName("Authorization").description("인증을 위한 Bearer 토큰")
        );
    }

    private static Snippet petDTORequestSnippet() {
        return PayloadDocumentation.requestFields(
            fieldWithPath("pet_name").description("실종 동물의 이름"),
            fieldWithPath("pet_gender").description("실종 동물의 성별"),
            fieldWithPath("birth_month.birth_month").description("실종 동물의 개월수"),
            fieldWithPath("pet_breed").description("실종 동물의 종"),
            fieldWithPath("lost_time").description("실종 시각"),
            fieldWithPath("location.latitude").description("실종 위도"),
            fieldWithPath("location.longitude").description("실종 경도"),
            fieldWithPath("pet_features").description("실종 동물의 특징"),
            fieldWithPath("is_neutering").description("중성화 여부")
        );
    }

    @Nested
    @DisplayName("[같이 찾기] registerMissingPet 테스트")
    class registerMissingPet {

        @Test
        @DisplayName("registerMissingPet 201 Created")
        void registerMissingPet_201() throws Exception {
            // when
            when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(oauthUserDTO);

            // then
            mockMvc.perform(
                    post("/api/missing")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer testTokens")
                        .content(toJson(missingPetDTO))
                )
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        authorizationSnippet(),
                        petDTORequestSnippet()
                    )
                );

            verify(missingService, times(1))
                .registerMissingPet(any(OauthUserDTO.class), any(MissingPetDTO.class));
        }

        @Nested
        @DisplayName("registerMissingPet 400 Bad Request")
        class registerMissingPet_400 {

            @ParameterizedTest
            @DisplayName("Age에 0이하의 값이 입력된 경우")
            @ValueSource(ints = {-100, -10, 0})
            void under0Age(int petBirthMonth) throws Exception {
                //given
                missingPetDTO = new MissingPetDTO(
                    "testPetName",
                    "testPetGender",
                    new BirthMonthDTO(petBirthMonth), // 애완동물의 개월수에 따른 오류 발생 체크
                    "testPetSpecies",
                    LocalDateTime.of(2024, 9, 25, 13, 19, 33),
                    new LocationDTO(
                        35.17F, 126.90F
                    ),
                    "testPetFeature",
                    true
                );

                // when
                when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                    .thenReturn(oauthUserDTO);

                doThrow(invalidPetBirthMonthException())
                    .when(missingService)
                    .registerMissingPet(
                        any(oauthUserDTO.getClass()),
                        any(missingPetDTO.getClass())
                    );

                // then
                mockMvc.perform(
                        post("/api/missing")
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer testTokens")
                            .content(toJson(missingPetDTO))
                    )
                    .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(APPLICATION_JSON),
                        content().json(toJson(new ErrorResponse(INVALID_PET_MONTH)))
                    )
                    .andDo(restDocs.document(
                        authorizationSnippet(),
                        petDTORequestSnippet()
                    ));

                verify(missingService, times(1))
                    .registerMissingPet(any(OauthUserDTO.class), any(MissingPetDTO.class));
            }

            @ParameterizedTest
            @DisplayName("날짜가 현재보다 늦은 경우")
            @MethodSource("lateMoreThanNowMethodSource")
            void lateMoreThanNow(LocalDateTime lostTime) throws Exception {
                //given
                missingPetDTO = new MissingPetDTO(
                    "testPetName",
                    "testPetGender",
                    new BirthMonthDTO(1),
                    "testPetSpecies",
                    lostTime,
                    new LocationDTO(35.17F, 126.90F),
                    "testPetFeature",
                    true
                );

                // when
                when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                    .thenReturn(oauthUserDTO);

                doThrow(invalidDateException())
                    .when(missingService)
                    .registerMissingPet(
                        any(oauthUserDTO.getClass()),
                        any(missingPetDTO.getClass())
                    );

                // then
                mockMvc.perform(
                        post("/api/missing")
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer testTokens")
                            .content(toJson(missingPetDTO))
                    )
                    .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(APPLICATION_JSON),
                        content().json(toJson(new ErrorResponse(INVALID_DATE)))
                    )
                    .andDo(restDocs.document(
                            authorizationSnippet(),
                            petDTORequestSnippet()
                        )
                    );
            }

            private static Stream<LocalDateTime> lateMoreThanNowMethodSource() {
                return Stream.of(
                    LocalDateTime.now().plusHours(1L),
                    LocalDateTime.now().plusMinutes(10L)
                );
            }

            @ParameterizedTest
            @DisplayName("위도 및 경도의 범위가 올바르지 않은 경우")
            @MethodSource("incorrectRangeOfLatitudeAndLongitudeMethodSource")
            void incorrectRangeOfLatitudeAndLongitude(float lostLatitude, float lostLongitude)
                throws Exception {
                // 위도는 -90 ~ 90도 사이
                // 경도는 -180 ~ 180도 사이의 범위를 가져야만 한다.
                //given
                missingPetDTO = new MissingPetDTO(
                    "testPetName",
                    "testPetGender",
                    new BirthMonthDTO(1),
                    "testPetSpecies",
                    LocalDateTime.of(2024, 9, 25, 13, 19, 33),
                    new LocationDTO(
                        lostLatitude,
                        lostLongitude
                    ),
                    "testPetFeature",
                    true
                );

                // when
                when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                    .thenReturn(oauthUserDTO);

                doThrow(invalidLocaltionException())
                    .when(missingService)
                    .registerMissingPet(
                        any(oauthUserDTO.getClass()),
                        any(missingPetDTO.getClass())
                    );

                // then
                mockMvc.perform(
                        post("/api/missing")
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer testTokens")
                            .content(toJson(missingPetDTO))
                    )
                    .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(APPLICATION_JSON),
                        content().json(toJson(new ErrorResponse(INVALID_LOCATION)))
                    )
                    .andDo(restDocs.document(
                            authorizationSnippet(),
                            petDTORequestSnippet()
                        )
                    );
            }

            private static Stream<Arguments> incorrectRangeOfLatitudeAndLongitudeMethodSource() {
                return Stream.of(
                    Arguments.of(-91, 0),
                    Arguments.of(91, 0),
                    Arguments.of(0, -181),
                    Arguments.of(0, 181),
                    Arguments.of(-91, 181),
                    Arguments.of(-90.00001F, 0)
                );
            }
        }

        @Nested
        @DisplayName("registerMissingPet 401 Unauthorization")
        @DirtiesContext(classMode = BEFORE_CLASS)
        class registerMissingPet_401 {

            @BeforeEach
            public void setup() {
                when(oauthUserArgumentResolver.supportsParameter(any()))
                    .thenReturn(true);
            }

            @Test
            @DisplayName("not include Bearer")
            public void notIncludeBearer() throws Exception {
                // when
                when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                    .thenThrow(invalidTokenException());

                // then
                mockMvc.perform(
                        post("/api/missing")
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "testTokens")
                            .content(toJson(missingPetDTO))
                    )
                    .andExpectAll(
                        status().isUnauthorized(),
                        content().contentType(APPLICATION_JSON),
                        content().json(toJson(new ErrorResponse(tokenInvalid)))
                    )
                    .andDo(restDocs.document(
                            authorizationSnippet(),
                            petDTORequestSnippet()
                        )
                    );

                verify(oauthUserArgumentResolver, times(1)).resolveArgument(any(), any(), any(),
                    any());
            }

            @Test
            @DisplayName("expired Token")
            public void expiredToken() throws Exception {
                // when
                when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                    .thenThrow(expiredTokenException());

                // then
                mockMvc.perform(
                        post("/api/missing")
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "bearer testTokens")
                            .content(toJson(missingPetDTO))
                    )
                    .andExpectAll(
                        status().isUnauthorized(),
                        content().contentType(APPLICATION_JSON),
                        content().json(toJson(new ErrorResponse(EXPIRED_TOKEN)))
                    ).andDo(restDocs.document(
                            authorizationSnippet(),
                            petDTORequestSnippet()
                        )
                    );
            }

            @Test
            @DisplayName("invalid user")
            public void invalidUser() throws Exception {
                // when
                when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                    .thenThrow(invalidUserException());

                // then
                mockMvc.perform(
                        post("/api/missing")
                            .contentType(APPLICATION_JSON)
                            .header("Authorization", "Bearer testTokens")
                            .content(toJson(missingPetDTO))
                    )
                    .andExpectAll(
                        status().isUnauthorized(),
                        content().contentType(APPLICATION_JSON),
                        content().json(toJson(new ErrorResponse(INVALID_USER)))
                    ).andDo(restDocs.document(
                            authorizationSnippet(),
                            petDTORequestSnippet()
                        )
                    );
            }

        }

        @Test
        @DisplayName("registerMissingPet 404 Not Found")
        void registerMissingPet_404() throws Exception {
            // when
            when(oauthUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(oauthUserDTO);

            doThrow(breedNotFoundException())
                .when(missingService)
                .registerMissingPet(any(oauthUserDTO.getClass()), any(missingPetDTO.getClass()));

            // then
            mockMvc.perform(
                    post("/api/missing")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer testTokens")
                        .content(toJson(missingPetDTO))
                )
                .andExpectAll(
                    status().isNotFound(),
                    content().contentType(APPLICATION_JSON),
                    content().json(toJson(new ErrorResponse(BREED_NOT_FOUND)))
                )
                .andDo(restDocs.document(
                    authorizationSnippet(),
                    petDTORequestSnippet()
                ));
        }
    }
}