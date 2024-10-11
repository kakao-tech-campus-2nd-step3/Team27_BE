package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_USER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.exception.ErrorMessage;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetService petService;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("이메일을 통해 사용자 찾기 테스트/findUserByEmail")
    class 이메일을_통해_사용자_찾기 {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            String email = "test@test.com";
            User expectUser = new User(email);

            // when
            when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(expectUser));

            // then
            assertEquals(expectUser, userService.findUserByEmail(email));

            verify(userRepository, times(1))
                .findByEmail(email);
        }

        @Test
        @DisplayName("실패")
        void 실패() {
            // given
            String email = "test@test.com";

            // when
            when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

            // then
            CustomException thrown = assertThrows(
                CustomException.class,
                () -> userService.findUserByEmail(email)
            );

            assertAll(
                () -> assertEquals(INVALID_USER, thrown.getErrorMessage()),
                () -> assertEquals(UNAUTHORIZED, thrown.getStatus())
            );

            verify(userRepository, times(1))
                .findByEmail(email);
        }
    }
}