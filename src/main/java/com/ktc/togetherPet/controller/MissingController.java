package com.ktc.togetherPet.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.ktc.togetherPet.annotation.OauthUser;
import com.ktc.togetherPet.model.dto.missing.MissingPetDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetNearByDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.service.MissingService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<MissingPetNearByDTO>> getMissingPetsNearByRegion(
        @RequestParam("latitude") float latitude,
        @RequestParam("longitude") float longitude
    ) {
        return ResponseEntity
            .status(OK)
            .body(missingService.getMissingPetsNearBy(latitude, longitude));
    }
}
