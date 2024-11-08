package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_USER;
import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.PET;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.user.UserInfoResponseDTO;
import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetService petService;

    @Mock
    private ImageService imageService;

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

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("이메일을 통해 사용자 존재여부 찾기 테스트/userExists")
    void 이메일을_통해_사용자_존재여부_찾기(boolean expects) {
        // given
        String email = "test@test.com";

        // when
        when(userRepository.existsByEmail(email))
            .thenReturn(expects);

        // then
        assertEquals(expects, userService.userExists(email));
    }

    @Test
    @DisplayName("유저 생성/createUser")
    void 유저_생성() {
        // given
        String email = "test@test.com";

        // when & then
        userService.createUser(email);

        verify(userRepository, times(1))
            .save(new User(email));
    }

    @Test
    @DisplayName("사용자의 애완동물 등록 테스트/setUserPet")
    void 사용자의_애완동물_등록() {
        // given
        long petId = 1L;
        String email = "test@test.com";
        User expectUser = new User(email);
        Pet expectPet = new Pet(
            "testPet",
            1L,
            new Breed("testBreed"),
            true
        );

        // when
        when(userRepository.findByEmail(email))
            .thenReturn(Optional.of(expectUser));

        when(petService.findPetById(petId))
            .thenReturn(expectPet);

        // then
        userService.setUserPet(petId, email);

        verify(userRepository, times(1))
            .findByEmail(email);

        verify(petService, times(1))
            .findPetById(petId);

        verify(userRepository, times(1))
            .save(expectUser);
    }

    @Test
    @DisplayName("사용자의 이름 등록 테스트/setUserName")
    void 사용자의_이름_등록() {
        // given
        String email = "test@test.com";
        String userName = "testName";

        User expectUser = new User(email);

        // when
        when(userRepository.findByEmail(email))
            .thenReturn(Optional.of(expectUser));

        // then
        userService.setUserName(email, userName);

        verify(userRepository, times(1))
            .findByEmail(email);

        verify(userRepository, times(1))
            .save(expectUser);
    }

    @Test
    @DisplayName("사용자의 정보 가져오기 테스트/getUserInfo")
    void 사용자의_정보_가져오기(){
        // given
        OauthUserDTO givenOauthUserDTO = new OauthUserDTO("test@email.com");
        User givenUser = new User("test@email.com");
        Pet givenPet = spy(new Pet(
            "testPetName",
            1L,
            new Breed("testBreed"),
            true
        ));
        givenUser.setPet(givenPet);
        givenUser.setName("testUserName");

        String expectImageUrl = "https://together-pet/images/test-uuid.jpg";

        UserInfoResponseDTO expectUserInfoResponseDTO = new UserInfoResponseDTO(
            givenUser.getName(),
            givenPet.getName(),
            expectImageUrl,
            givenPet.getBirthMonth()
        );

        // when
        when(userRepository.findByEmail(givenUser.getEmail()))
            .thenReturn(Optional.of(givenUser));

        when(givenPet.getId())
            .thenReturn(1L);

        when(imageService.getRepresentativeImageById(PET, 1L))
            .thenReturn(expectImageUrl);

        // then
        assertEquals(expectUserInfoResponseDTO, userService.getUserInfo(givenOauthUserDTO));

        verify(userRepository, times(1))
            .findByEmail(givenUser.getEmail());

        verify(imageService, times(1))
            .getRepresentativeImageById(PET, 1L);
    }
}