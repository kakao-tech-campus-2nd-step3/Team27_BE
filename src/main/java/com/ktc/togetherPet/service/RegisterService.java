package com.ktc.togetherPet.service;

import com.ktc.togetherPet.model.dto.pet.PetRegisterDTO;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RegisterService {

    private final PetService petService;
    private final ImageService imageService;
    private final UserService userService;

    public RegisterService(PetService petService, ImageService imageService,
        UserService userService) {
        this.petService = petService;
        this.imageService = imageService;
        this.userService = userService;
    }

    @Transactional
    public void create(PetRegisterDTO petRegisterDTO,
        MultipartFile petImage, String email, String userName) throws IOException {
        Long petId = petService.createPet(petRegisterDTO);
        String imageFilePath = imageService.saveImage(petId, petImage);
        petService.setImageSrc(petId, imageFilePath);

        userService.setUserPet(petId, email);
        userService.setUserName(email, userName);
    }
}
