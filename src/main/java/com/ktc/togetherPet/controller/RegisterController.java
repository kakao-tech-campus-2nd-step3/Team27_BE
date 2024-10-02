package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.annotation.OauthUser;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.pet.PetRegisterDTO;
import com.ktc.togetherPet.service.RegisterService;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/register")
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService RegisterService) {
        this.registerService = RegisterService;
    }

    @PostMapping
    public ResponseEntity<?> registerPetUser(
        @RequestPart(value = "PetRegisterDTO") PetRegisterDTO petRegisterDTO,
        @RequestPart(value = "petImage") MultipartFile petImage,
        @RequestPart(value = "userName") String userName,
        @OauthUser OauthUserDTO oauthUserDTO) throws IOException {
        registerService.create(petRegisterDTO, petImage, oauthUserDTO.email(), userName);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
