package com.ktc.togetherPet.controller;

import static com.ktc.togetherPet.exception.CustomException.breedNotFoundException;
import static com.ktc.togetherPet.exception.CustomException.invalidDateException;
import static com.ktc.togetherPet.exception.CustomException.invalidLocaltionException;
import static com.ktc.togetherPet.exception.CustomException.invalidPetBirthMonthException;
import static com.ktc.togetherPet.exception.CustomException.invalidUserException;
import static com.ktc.togetherPet.exception.ErrorMessage.BREED_NOT_FOUND;
import static com.ktc.togetherPet.exception.ErrorMessage.EXPIRED_TOKEN;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_DATE;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_LOCATION;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_PET_MONTH;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.missing.MissingPetDTO;
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

@WebMvcTest(MissingController.class)
class MissingControllerTest extends RestDocsTestSupport {

    // TODO 인증 관련 로직 완성 후 수정 필요
    @MockBean
    private AuthorizationArgumentResolver authorizationArgumentResolver;

    @MockBean
    private MissingService missingService;

    //given
    // TODO 인증 관련 로직 완성 후 수정 필요
    private UserDTO userDTO;
    private MissingPetDTO missingPetDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO(1, "test@email.com");
        missingPetDTO = new MissingPetDTO(
            "testPetName",
            "testPetGender",
            1,
            "testPetSpecies",
            LocalDateTime.of(2024, 9, 25, 13, 19, 33),
            35.17F,
            126.90F,
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
            fieldWithPath("pet_age").description("실종 동물의 개월수"),
            fieldWithPath("pet_species").description("실종 동물의 종"),
            fieldWithPath("lost_time").description("실종 시각"),
            fieldWithPath("lost_latitude").description("실종 위도"),
            fieldWithPath("lost_longitude").description("실종 경도"),
            fieldWithPath("pet_features").description("실종 동물의 특징")
        );
    }

    @Nested
    @DisplayName("[같이 찾기] registerMissingPet 테스트")
    class registerMissingPet {

        @Test
        @DisplayName("registerMissingPet 201 Created")
        void registerMissingPet_201() throws Exception {
            // when
            when(authorizationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(userDTO);

            // then
            mockMvc.perform(
                    post("/api/missing")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization: Bearer testTokens")
                        .content(toJson(missingPetDTO))
                )
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        authorizationSnippet(),
                        petDTORequestSnippet()
                    )
                );
        }

        @Nested
        @DisplayName("registerMissingPet 400 Bad Request")
        class registerMissingPet_400 {

            @ParameterizedTest
            @DisplayName("Age에 0이하의 값이 입력된 경우")
            @ValueSource(ints = {-100, -10, 0})
            void under0Age(int petAge) throws Exception {
                //given
                missingPetDTO = new MissingPetDTO(
                    "testPetName",
                    "testPetGender",
                    petAge, // 애완동물의 개월수에 따른 오류 발생 체크
                    "testPetSpecies",
                    LocalDateTime.of(2024, 9, 25, 13, 19, 33),
                    35.17F,
                    126.90F,
                    "testPetFeature",
                    true
                );

                // when
                when(authorizationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                    .thenReturn(userDTO));

                doThrow(invalidPetBirthMonthException())
                    .when(missingService)
                    .registerMissingPet(userDTO, missingPetDTO);

                // then
                mockMvc.perform(
                        post("/api/missing")
                            .contentType(APPLICATION_JSON)
                            .header("Authorization: Bearer testTokens")
                            .content(toJson(missingPetDTO))
                    )
                    .andExpectAll(
                        status().isBadRequest(),
                        // TODO Error Message Template 재정의 후 contentType 선정
                        // content().contentType(APPLICATION_JSON),
                        content().json(toJson(INVALID_PET_MONTH))
                    )
                    .andDo(restDocs.document(
                        authorizationSnippet(),
                        petDTORequestSnippet()
                    ));
            }

            @ParameterizedTest
            @DisplayName("날짜가 현재보다 늦은 경우")
            @MethodSource("lateMoreThanNowMethodSource")
            void lateMoreThanNow(LocalDateTime lostTime) throws Exception {
                //given
                missingPetDTO = new MissingPetDTO(
                    "testPetName",
                    "testPetGender",
                    1,
                    "testPetSpecies",
                    lostTime,
                    35.17F,
                    126.90F,
                    "testPetFeature",
                    true
                );

                // when
                when(authorizationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                    .thenReturn(userDTO);

                doThrow(invalidDateException())
                    .when(missingService)
                    .registerMissingPet(userDTO, missingPetDTO);

                // then
                mockMvc.perform(
                        post("/api/missing")
                            .contentType(APPLICATION_JSON)
                            .header("Authorization: Bearer testTokens")
                            .content(toJson(missingPetDTO))
                    )
                    .andExpectAll(
                        status().isBadRequest(),
                        // TODO Error Message Template 재정의 후 contentType 선정
                        // content().contentType(APPLICATION_JSON),
                        content().json(toJson(INVALID_DATE))
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
                    1,
                    "testPetSpecies",
                    LocalDateTime.of(2024, 9, 25, 13, 19, 33),
                    lostLatitude,
                    lostLongitude,
                    "testPetFeature",
                    true
                );

                // when
                when(authorizationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                    .thenReturn(userDTO);

                doThrow(invalidLocaltionException())
                    .when(missingService)
                    .registerMissingPet(userDTO, missingPetDTO);

                // then
                mockMvc.perform(
                        post("/api/missing")
                            .contentType(APPLICATION_JSON)
                            .header("Authorization: Bearer testTokens")
                            .content(toJson(missingPetDTO))
                    )
                    .andExpectAll(
                        status().isBadRequest(),
                        // TODO Error Message Template 재정의 후 contentType 선정
                        // content().contentType(APPLICATION_JSON),
                        content().json(toJson(INVALID_LOCATION))
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
                    Arguments.of(-90.0000001, 0)
                );
            }
        }

        @Nested
        @DisplayName("registerMissingPet 401 Unauthorization")
        class registerMissingPet_401 {

            @Test
            @DisplayName("not include Bearer")
            public void notIncludeBearer() throws Exception {
                // when
                when(authorizationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                    .thenThrow(invalidUserException());

                // then
                mockMvc.perform(
                        post("/api/missing")
                            .contentType(APPLICATION_JSON)
                            .header("Authorization: testTokens")
                            .content(toJson(missingPetDTO))
                    )
                    .andExpectAll(
                        status().isUnauthorized(),
                        // TODO Error Message Template 재정의 후 contentType 선정
                        // content().contentType(APPLICATION_JSON),
                        content().json(toJson(INVALID_USER))
                    )
                    .andDo(restDocs.document(
                            authorizationSnippet(),
                            petDTORequestSnippet()
                        )
                    );
            }

            @Test
            @DisplayName("expired Token")
            public void expiredToken() throws Exception {
                // when
                when(authorizationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                    .thenThrow(CustomException::expiredTokenException);

                // then
                mockMvc.perform(
                        post("/api/missing")
                            .contentType(APPLICATION_JSON)
                            .header("Authorization: testTokens")
                            .content(toJson(missingPetDTO))
                    )
                    .andExpectAll(
                        status().isUnauthorized(),
                        // TODO Error Message Template 재정의 후 contentType 선정
                        // content().contentType(APPLICATION_JSON),
                        content().json(toJson(EXPIRED_TOKEN))
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
                when(authorizationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                    .thenThrow(CustomException::invalidUserException);

                // then
                mockMvc.perform(
                        post("/api/missing")
                            .contentType(APPLICATION_JSON)
                            .header("Authorization: testTokens")
                            .content(toJson(missingPetDTO))
                    )
                    .andExpectAll(
                        status().isUnauthorized(),
                        // TODO Error Message Template 재정의 후 contentType 선정
                        // content().contentType(APPLICATION_JSON),
                        content().json(toJson(INVALID_USER))
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
            when(authorizationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(userDTO);

            doThrow(breedNotFoundException())
                .when(missingService)
                .registerMissingPet(userDTO, missingPetDTO);

            // then
            mockMvc.perform(
                    post("/api/missing")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization: Bearer testTokens")
                )
                .andExpectAll(
                    status().isBadRequest(),
                    // TODO Error Message Template 재정의 후 contentType 선정
                    // content().contentType(APPLICATION_JSON),
                    content().json(toJson(BREED_NOT_FOUND))
                )
                .andDo(restDocs.document(
                    authorizationSnippet(),
                    petDTORequestSnippet()
                ));
        }
    }
}