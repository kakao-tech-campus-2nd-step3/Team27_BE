package com.ktc.togetherPet.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.ktc.togetherPet.annotation.OauthUser;
import com.ktc.togetherPet.apiResponse.CustomResponse;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.report.ReportCreateRequestDTO;
import com.ktc.togetherPet.model.dto.report.ReportDetailResponseDTO;
import com.ktc.togetherPet.model.dto.report.ReportResponseDTO;
import com.ktc.togetherPet.service.ReportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping(consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createReport(
        @RequestPart ReportCreateRequestDTO reportCreateRequestDTO,
        @RequestPart(required = false) List<MultipartFile> files,
        @OauthUser OauthUserDTO oauthUserDTO
    ) {
        reportService.createReport(reportCreateRequestDTO, files, oauthUserDTO);
        return CustomResponse.created();
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReportResponseDTO>> getReceivedReports(
        @OauthUser OauthUserDTO oauthUserDTO
    ) {
        return CustomResponse.ok(
            reportService.getReceivedReports(oauthUserDTO)
        );
    }

    @GetMapping("/location")
    public ResponseEntity<List<ReportResponseDTO>> getNearByReports(
        @RequestParam("latitude") double latitude,
        @RequestParam("longitude") double longitude
    ) {
        return CustomResponse.ok(
            reportService.getReportsByLocation(latitude, longitude)
        );
    }

    @GetMapping("/{report-id}")
    public ResponseEntity<ReportDetailResponseDTO> getReportDetail(
        @PathVariable("report-id") Long reportId
    ) {
        return CustomResponse.ok(
            reportService.getReportById(reportId)
        );
    }

}