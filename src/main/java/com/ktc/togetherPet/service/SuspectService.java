package com.ktc.togetherPet.service;

import com.ktc.togetherPet.model.dto.suspect.SuspectRequestDTO;
import com.ktc.togetherPet.model.entity.Report;
import com.ktc.togetherPet.repository.ReportRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SuspectService {

    private final ReportRepository reportRepository;

    public SuspectService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public void createMissing(SuspectRequestDTO suspectRequestDTO, List<MultipartFile> files) {
        /** todo: Report 엔티티에 species / color 값이 nullable로 추가되어야 함
         */

        Report report = new Report();
    }
}
