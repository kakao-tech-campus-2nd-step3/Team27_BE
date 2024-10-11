package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.annotation.OauthUser;
import com.ktc.togetherPet.apiResponse.CustomResponse;
import com.ktc.togetherPet.model.dto.missing.MissingPetDetailResponseDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetNearByResponseDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetRequestDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.service.MissingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v0/missing")
@RequiredArgsConstructor
public class MissingController {

    private final MissingService missingService;

    @PostMapping
    public ResponseEntity<?> registerMissingPet(
        @OauthUser OauthUserDTO oauthUserDTO,
        @RequestBody MissingPetRequestDTO missingPetRequestDTO
    ) {

        missingService.registerMissingPet(oauthUserDTO, missingPetRequestDTO);
        return CustomResponse.created();
    }

    @GetMapping
    public ResponseEntity<List<MissingPetNearByResponseDTO>> getMissingPetsNearByRegion(
        @RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude
    ) {
        return CustomResponse.ok(missingService.getMissingPetsNearBy(latitude, longitude));
    }

    @GetMapping("/{missing-id}")
    public ResponseEntity<MissingPetDetailResponseDTO> getMissingPetDetailByMissingId(
        @PathVariable("missing-id") long missingId
    ) {
        return CustomResponse.ok(missingService.getMissingPetDetailByMissingId(missingId));
    }
}
