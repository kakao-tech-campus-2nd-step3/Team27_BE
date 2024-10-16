package com.ktc.togetherPet.model.vo;

import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_LOCATION;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.ktc.togetherPet.exception.CustomException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LocationTest {

    @Nested
    @DisplayName("위도 및 경도 입력에 따른 테스트")
    class 위도_및_경도_입력 {

        @ParameterizedTest
        @MethodSource("위도_및_경도가_올바른_경우_methodsource")
        @DisplayName("위도가 -90~90, 경도가 -180~180사이의 범위를 가지는 경우")
        void 위도_및_경도가_올바른_경우(double latitude, double longitude) {
            assertDoesNotThrow(
                () -> new Location(latitude, longitude)
            );
        }

        private static Stream<Arguments> 위도_및_경도가_올바른_경우_methodsource() {
            return Stream.of(
                Arguments.of(-15.0D, -15.D),
                Arguments.of(-90D, -180D),
                Arguments.of(-90D, 180D),
                Arguments.of(90D, 180D),
                Arguments.of(90D, -180D)
            );
        }

        @ParameterizedTest
        @MethodSource("위도_및_경도가_올바르지_않은_경우_methodsource")
        @DisplayName("위도가 -90 미만 90초과, 경도가 -180 미만 180 초과의 범위를 가진 경우")
        void 위도_및_경도가_올바르지_않은_경우(double latitude, double longitude) {
            CustomException thrown = assertThrows(
                CustomException.class,
                () -> new Location(latitude, longitude)
            );

            assertAll(
                () -> assertEquals(INVALID_LOCATION, thrown.getErrorMessage()),
                () -> assertEquals(BAD_REQUEST, thrown.getStatus())
            );
        }

        private static Stream<Arguments> 위도_및_경도가_올바르지_않은_경우_methodsource() {
            return Stream.of(
                Arguments.of(-91D, 15D),
                Arguments.of(91D, 15D),
                Arguments.of(15D, -181D),
                Arguments.of(15D, 181D),
                Arguments.of(90.00000000000001D, -15D),
                Arguments.of(-90.0000000000001D, 15D),
                Arguments.of(15D, 180.0000000000001D),
                Arguments.of(15D, -180.0000000000001D)
            );
        }
    }
}