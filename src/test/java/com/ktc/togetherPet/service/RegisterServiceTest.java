package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.PET;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import com.ktc.togetherPet.model.dto.pet.PetRegisterRequestDTO;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private PetService petService;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private RegisterService registerService;

    @Test
    @DisplayName("애완동물과 사용자의 정보 등록 테스트/create")
    void 애완동물과_사용자의_정보_등록() {
        // given
        PetRegisterRequestDTO petRegisterRequestDTO = new PetRegisterRequestDTO(
            "testPetName",
            1,
            "testBreed",
            true,
            "testPetFeature"
        );

        MultipartFile petImage = new MockMultipartFile(
            "testPetImage",
            "testPetImage.jpeg",
            IMAGE_JPEG_VALUE,
            "testPetImage.jpeg".getBytes()
        );

        String email = "test@test.com";

        String userName = "testUserName";

        long expectPetId = 1L;

        // when
        when(petService.createPet(petRegisterRequestDTO))
            .thenReturn(expectPetId);

        // then
        registerService.create(petRegisterRequestDTO, petImage, email, userName);

        verify(petService, times(1))
            .createPet(petRegisterRequestDTO);

        verify(imageService, times(1))
            .saveImages(expectPetId, PET, List.of(petImage));

        verify(userService, times(1))
            .setUserPet(expectPetId, email);

        verify(userService, times(1))
            .setUserName(email, userName);
    }
}