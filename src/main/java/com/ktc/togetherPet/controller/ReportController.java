package com.ktc.togetherPet.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.ktc.togetherPet.annotation.OauthUser;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.report.ReportCreateRequestDTO;
import com.ktc.togetherPet.model.dto.report.ReportResponseDTO;
import com.ktc.togetherPet.model.dto.suspect.ReportDetailResponseDTO;
import com.ktc.togetherPet.model.dto.suspect.ReportNearByResponseDTO;
import com.ktc.togetherPet.service.MissingService;
import com.ktc.togetherPet.service.ReportService;
import com.ktc.togetherPet.service.SuspectService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v0/report")
@RequiredArgsConstructor
public class ReportController {

    private final MissingService missingService;
    private final SuspectService suspectService;
    private final ReportService reportService;

    @PostMapping(consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> createReport(
        @RequestPart ReportCreateRequestDTO reportCreateRequestDTO,
        @RequestPart(required = false) List<MultipartFile> files,
        @OauthUser OauthUserDTO oauthUserDTO
    ) {
        reportService.createReport(reportCreateRequestDTO, files, oauthUserDTO);
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReportResponseDTO>> getPersonalReports(
        @OauthUser OauthUserDTO oauthUserDTO
    ) {
        return ResponseEntity
            .status(OK)
            .body(missingService.getMissingReports(oauthUserDTO));
    }

    @GetMapping("/location")
    public ResponseEntity<List<ReportNearByResponseDTO>> getNearByReports(
        @RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(suspectService.getSuspectReportsNearBy(latitude, longitude));
    }

    @GetMapping("/{report-id}")
    public ResponseEntity<ReportDetailResponseDTO> getReportDetail(@PathVariable Long reportId) {
        return null;
//        return ResponseEntity
//            .status(OK)
//            .body(missingService.getReportDetail(reportId));
    }

}
// 제보 등록
// 자신에게 온 실종 제보 확인
// 근처 실종 제보 확인
// 제보 디테일 확인