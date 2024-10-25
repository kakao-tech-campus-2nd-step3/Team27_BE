package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.PET;

import com.ktc.togetherPet.model.dto.pet.PetRegisterRequestDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final PetService petService;
    private final ImageService imageService;
    private final UserService userService;

    @Transactional
    public void create(
        PetRegisterRequestDTO petRegisterDTO,
        MultipartFile petImage,
        String email,
        String userName
    ) {
        Long petId = petService.createPet(petRegisterDTO);

        imageService.saveImages(petId, PET, List.of(petImage));
        userService.setUserPet(petId, email);
        userService.setUserName(email, userName);
    }
}
