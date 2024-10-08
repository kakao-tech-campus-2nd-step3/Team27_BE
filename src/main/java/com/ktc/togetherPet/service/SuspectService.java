package com.ktc.togetherPet.service;

import com.ktc.togetherPet.model.dto.suspect.ReportDetailResponseDTO;
import com.ktc.togetherPet.repository.ReportRepository;
import com.ktc.togetherPet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuspectService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final ReportService reportService;

    public ReportDetailResponseDTO getSuspectReportDetailByReportId(long reportId) {
        return reportService.getReportById(reportId);
    }
}
