package com.ktc.togetherPet.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ktc.togetherPet.model.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("이메일을 통해 유저를 찾는 테스트/findByEmail")
    class 이메일을_통해_유저를_찾기 {

        @Test
        @DisplayName("유저가 존재하는 경우")
        void 유저가_존재하는_경우() {
            // given
            String findUserEmail = "찾고자 하는 유저";

            List<User> givenUser = List.of(
                new User(findUserEmail),
                new User("이건 다른 유저 1"),
                new User("이건 다른 유저 2")
            );

            // when
            userRepository.saveAll(givenUser);

            // then
            Optional<User> actual = userRepository.findByEmail(findUserEmail);

            assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(givenUser.getFirst(), actual.get())
            );
        }

        @Test
        @DisplayName("유저가 존재하지 않는 경우")
        void 유저가_존재하지_않는_경우() {
            // given
            String findUserEmail = "찾고자 하는 유저";

            List<User> givenUser = List.of(
                new User("이건 다른 유저 1"),
                new User("이건 다른 유저 2")
            );

            // when
            userRepository.saveAll(givenUser);

            // then
            Optional<User> actual = userRepository.findByEmail(findUserEmail);

            assertTrue(actual.isEmpty());
        }
    }

    @Nested
    @DisplayName("이메일을 통해서 사용자가 존재하는지 확인하는 테스트/existsByEmail")
    class 이메일을_통해서_사용자가_존재하는지_확인 {

        @Test
        @DisplayName("사용자가 존재하는 경우")
        void 사용자가_존재하는_경우() {
            // given
            String findUserEmail = "찾고자 하는 유저";

            List<User> givenUser = List.of(
                new User(findUserEmail),
                new User("이건 다른 유저 1"),
                new User("이건 다른 유저 2")
            );

            // when
            userRepository.saveAll(givenUser);

            // then
            boolean actual = userRepository.existsByEmail(findUserEmail);

            assertTrue(actual);
        }


        @Test
        @DisplayName("사용자가 존재하지 않는 경우")
        void 사용자가_존재하지_않는_경우() {
            // given
            String findUserEmail = "찾고자 하는 유저";

            List<User> givenUser = List.of(
                new User("이건 다른 유저 1"),
                new User("이건 다른 유저 2")
            );

            // when
            userRepository.saveAll(givenUser);

            // then
            boolean actual = userRepository.existsByEmail(findUserEmail);

            assertFalse(actual);
        }
    }
}