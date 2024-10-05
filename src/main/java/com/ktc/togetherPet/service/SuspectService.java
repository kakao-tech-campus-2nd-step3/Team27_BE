package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.REPORT;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.suspect.ReportDetailDTO;
import com.ktc.togetherPet.model.dto.suspect.ReportNearByDTO;
import com.ktc.togetherPet.model.dto.suspect.SuspectRequestDTO;
import com.ktc.togetherPet.model.entity.Report;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.repository.ReportRepository;
import com.ktc.togetherPet.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SuspectService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final ReportService reportService;

    public SuspectService(ReportRepository reportRepository, UserRepository userRepository,
        ImageService imageService, ReportService reportService) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.reportService = reportService;
    }

    @Transactional
    public void createSuspectReport(
        OauthUserDTO oauthUserDTO,
        SuspectRequestDTO suspectRequestDTO,
        List<MultipartFile> files
    ) {
        User user = userRepository.findByEmail(oauthUserDTO.email())
            .orElseThrow(CustomException::invalidUserException);

        Long reportId = reportService.createReport(user, suspectRequestDTO);
        imageService.saveImages(reportId, REPORT, files);

        //todo: Optional.ofNullable 을 사용하는 것으로 변경
        if (suspectRequestDTO.breed() != null) {
            reportService.setBreed(reportId, suspectRequestDTO.breed());
        }
        if (suspectRequestDTO.gender() != null) {
            reportService.setGender(reportId, suspectRequestDTO.gender());
        }
        if (suspectRequestDTO.missingId() != null) {
            reportService.setMissing(reportId, suspectRequestDTO.missingId());
        }

        /** todo: Report 엔티티에 species / color 값이 nullable로 추가되어야 함
         */

        Report report = new Report();
    }

    public List<ReportNearByDTO> getSuspectReportsNearBy(float latitude, float longitude) {

        return reportService.getReportsByLocation(latitude, longitude);
    }

    public ReportDetailDTO getSuspectReportDetailByReportId(long reportId) {
        return reportService.getReportById(reportId);
    }
}
