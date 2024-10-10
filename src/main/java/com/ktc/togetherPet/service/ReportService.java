package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.REPORT;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.report.ReportCreateRequestDTO;
import com.ktc.togetherPet.model.dto.report.ReportDetailResponseDTO;
import com.ktc.togetherPet.model.dto.report.ReportNearByResponseDTO;
import com.ktc.togetherPet.model.dto.report.ReportResponseDTO;
import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.Report;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.ReportRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final MissingService missingService;
    private final KakaoMapService kakaoMapService;
    private final ImageService imageService;
    private final UserService userService;

    @Transactional
    public void createReport(
        ReportCreateRequestDTO reportCreateRequestDTO,
        List<MultipartFile> files,
        OauthUserDTO oauthUserDTO
    ) {
        User user = userService.findUserByEmail(oauthUserDTO.email());

        Location location = new Location(
            reportCreateRequestDTO.foundLatitude(),
            reportCreateRequestDTO.foundLongitude()
        );

        Report report = new Report(
            user,
            reportCreateRequestDTO.foundDate(),
            location,
            kakaoMapService.getRegionCodeFromKakao(location),
            reportCreateRequestDTO.description()
        );

        Optional.ofNullable(reportCreateRequestDTO.breed())
            .ifPresent(breed -> report.setBreed(new Breed(breed)));

        Optional.ofNullable(reportCreateRequestDTO.gender())
            .ifPresent(report::setGender);

        Optional.ofNullable(reportCreateRequestDTO.missingId())
            .ifPresent(missingId -> report.setMissing(missingService.findByMissingId(missingId)));

        long reportId = reportRepository.save(report).getId();
        imageService.saveImages(reportId, REPORT, files);
    }

    public List<ReportResponseDTO> getReceivedReports(OauthUserDTO oauthUserDTO) {
        User user = userService.findUserByEmail(oauthUserDTO.email());

        Pet pet = user.getPet();

        Missing missing = missingService.findByPet(pet);
        List<Report> reports = reportRepository.findAllByMissing(missing);

        return reports.stream()
            .map(report ->
                new ReportResponseDTO(
                    report.getId(),
                    report.getLocation().getLatitude(),
                    report.getLocation().getLongitude(),
                    imageService.getRepresentativeImageById(REPORT, report.getId())
                )
            ).toList();
    }

    public List<ReportNearByResponseDTO> getReportsByLocation(double latitude, double longitude) {
        Location location = new Location(latitude, longitude);
        long regionCode = kakaoMapService.getRegionCodeFromKakao(location);

        /** XXX: 이 부분 missing이 null인 경우에 받을 지 아니면 전체 리포트를 받을 지 상의 필요
         * missing이 null인 것만 받아옴 = 실종신고자에게 제보한 것은 제외함 = findAllByRegionCodeAndMissingNotNull
         * 전체 리포트를 받아옴 = 실종신고자에게 제보한 것도 포함함 = findAllByRegionCode
         **/
        return reportRepository.findAllByRegionCode(regionCode)
            .stream()
            .map(report -> {
                String representImagePath = imageService.getRepresentativeImageById(
                    REPORT, report.getId());
                return new ReportNearByResponseDTO(
                    report.getId(),
                    report.getLocation().getLatitude(),
                    report.getLocation().getLongitude(),
                    representImagePath
                );
            })
            .collect(Collectors.toList());

    }

    public ReportDetailResponseDTO getReportById(long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(CustomException::reportNotFoundException);

        Location location = report.getLocation();

        return new ReportDetailResponseDTO(
            report.getBreed().getName(),
            report.getColor(),
            report.getGender(),
            location.getLatitude(),
            location.getLongitude(),
            report.getDescription(),
            report.getUser().getName(),
            imageService.getImageUrl(reportId, REPORT),
            report.getTimeStamp()
        );
    }
}
