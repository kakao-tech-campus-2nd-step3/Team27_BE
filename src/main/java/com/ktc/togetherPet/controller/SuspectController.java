package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.annotation.OauthUser;
import com.ktc.togetherPet.apiResponse.CustomResponse;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.suspect.ReportDetailResponseDTO;
import com.ktc.togetherPet.model.dto.suspect.ReportNearByResponseDTO;
import com.ktc.togetherPet.model.dto.suspect.SuspectRequestDTO;
import com.ktc.togetherPet.service.SuspectService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/v0/missing/suspect")
@RequiredArgsConstructor
public class SuspectController {

    private final SuspectService suspectService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createSuspect(
        @RequestPart SuspectRequestDTO suspectRequestDTO,
        @RequestPart(required = false) List<MultipartFile> files,
        @OauthUser OauthUserDTO oauthUserDTO
    ) {
        suspectService.createSuspectReport(oauthUserDTO, suspectRequestDTO, files);

        return CustomResponse.created();
    }

    @GetMapping
    public ResponseEntity<List<ReportNearByResponseDTO>> getSuspectReportsNearByRegion(
        @RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude
    ) {
        return CustomResponse.ok(suspectService.getSuspectReportsNearBy(latitude, longitude));
    }

    @GetMapping("/{report-id}")
    public ResponseEntity<ReportDetailResponseDTO> getSuspectReportDetailByReportId(
        @PathVariable("report-id") long reportId
    ) {
        return CustomResponse.ok(suspectService.getSuspectReportDetailByReportId(reportId));
    }
}
