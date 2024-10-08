package com.ktc.togetherPet.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.ktc.togetherPet.annotation.OauthUser;
import com.ktc.togetherPet.model.dto.missing.MissingPetDetailResponseDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetNearByResponseDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetRequestDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.report.ReportResponseDTO;
import com.ktc.togetherPet.model.dto.report.ReportDetailResponseDTO;
import com.ktc.togetherPet.service.ImageService;
import com.ktc.togetherPet.service.MissingService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/missing")
public class MissingController {

    private final MissingService missingService;
    private final ImageService imageService;

    public MissingController(MissingService missingService, ImageService imageService) {
        this.missingService = missingService;
        this.imageService = imageService;
    }

    @PostMapping
    public ResponseEntity<Void> registerMissingPet(
        @OauthUser OauthUserDTO oauthUserDTO,
        @RequestBody MissingPetRequestDTO missingPetRequestDTO
    ) {

        missingService.registerMissingPet(oauthUserDTO, missingPetRequestDTO);
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<MissingPetNearByResponseDTO>> getMissingPetsNearByRegion(
        @RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude
    ) {
        return ResponseEntity
            .status(OK)
            .body(missingService.getMissingPetsNearBy(latitude, longitude));
    }

    @GetMapping("/{missing-id}")
    public ResponseEntity<MissingPetDetailResponseDTO> getMissingPetDetailByMissingId(
        @PathVariable("missing-id") long missingId
    ) {
        return ResponseEntity
            .status(OK)
            .body(missingService.getMissingPetDetailByMissingId(missingId));
    }

    @GetMapping("/report")
    public ResponseEntity<List<ReportResponseDTO>> getMissingReports(
        @OauthUser OauthUserDTO oauthUserDTO
    ) {
        return ResponseEntity
            .status(OK)
            .body(missingService.getMissingReports(oauthUserDTO));
    }

    @GetMapping("/report/{report-id}")
    public ResponseEntity<ReportDetailResponseDTO> getMissingReportDetailByReportId(
        @PathVariable("report-id") long reportId
    ) {
        return ResponseEntity
            .status(OK)
            .body(missingService.getReportDetail(reportId));
    }
}
