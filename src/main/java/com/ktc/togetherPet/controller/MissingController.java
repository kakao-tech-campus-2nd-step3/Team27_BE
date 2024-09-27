package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.annotation.OauthUser;
import com.ktc.togetherPet.model.dto.missing.MissingPetDTO;
import com.ktc.togetherPet.model.dto.user.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/missing")
public class MissingController {

    @PostMapping
    public ResponseEntity<Void> registerMissingPet(
        @OauthUser UserDTO userDTO,
        @RequestBody MissingPetDTO missingPetDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
