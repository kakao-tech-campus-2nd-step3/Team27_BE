package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.annotation.OauthUser;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.suspect.SuspectRequestDTO;
import com.ktc.togetherPet.service.SuspectService;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/missing/suspect")
public class SuspectController {

    private final SuspectService suspectService;

    public SuspectController(SuspectService suspectService) {
        this.suspectService = suspectService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createSuspect(
        @RequestPart SuspectRequestDTO suspectRequestDTO,
        @RequestPart(required = false) List<MultipartFile> files,
        @OauthUser OauthUserDTO oauthUserDTO
        ) throws IOException {
        suspectService.createSuspectReport(oauthUserDTO, suspectRequestDTO, files);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    //todo: tempDTO를 정의해야 함
    public ResponseEntity<List<tempDTO>> getSuspectReportsNearByRegion(
        @RequestParam("latitude") float latitude,
        @RequestParam("longitude") float longitude
        ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(suspectService.getSuspectReportsNearBy(latitude, longitude));
    }
}
