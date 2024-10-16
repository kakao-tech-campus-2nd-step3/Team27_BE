package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.annotation.OauthUser;
import com.ktc.togetherPet.apiResponse.CustomResponse;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.pet.PetRegisterRequestDTO;
import com.ktc.togetherPet.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping
    public ResponseEntity<?> registerPetUser(
        @RequestPart(value = "PetRegisterDTO") PetRegisterRequestDTO petRegisterRequestDTO,
        @RequestPart(value = "petImage") MultipartFile petImage,
        @RequestPart(value = "userName") String userName,
        @OauthUser OauthUserDTO oauthUserDTO) {
        registerService.create(petRegisterRequestDTO, petImage, oauthUserDTO.email(), userName);

        return CustomResponse.created();
    }
}
