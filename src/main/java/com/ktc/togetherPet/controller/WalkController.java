package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.annotation.OauthUser;
import com.ktc.togetherPet.apiResponse.CustomResponse;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.walk.CalorieResponseDTO;
import com.ktc.togetherPet.model.dto.walk.WalkRequestDTO;
import com.ktc.togetherPet.model.dto.walk.WalkResponseDTO;
import com.ktc.togetherPet.service.WalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v0/walk")
@RequiredArgsConstructor
public class WalkController {

    private final WalkService walkService;

    @PostMapping
    public ResponseEntity<CalorieResponseDTO> createWalk(
        @OauthUser OauthUserDTO oauthUserDTO,
        @RequestBody WalkRequestDTO walkRequestDTO
    ) {
        return CustomResponse.ok(walkService.createWalk(oauthUserDTO, walkRequestDTO));
    }

    @GetMapping
    public ResponseEntity<WalkResponseDTO> getWalkInformation(
        @OauthUser OauthUserDTO oauthUserDTO
    ) {
        return CustomResponse.ok(
            walkService.getWalkInformation(oauthUserDTO)
        );
    }
}
