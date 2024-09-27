package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.annotation.OauthUser;
import com.ktc.togetherPet.model.dto.missing.MissingPetDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.service.MissingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/missing")
public class MissingController {

    private final MissingService missingService;

    public MissingController(MissingService missingService) {
        this.missingService = missingService;
    }

    @PostMapping
    public ResponseEntity<Void> registerMissingPet(
        @OauthUser OauthUserDTO oauthUserDTO,
        @RequestBody MissingPetDTO missingPetDTO
    ) {
        missingService.registerMissingPet(oauthUserDTO, missingPetDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
