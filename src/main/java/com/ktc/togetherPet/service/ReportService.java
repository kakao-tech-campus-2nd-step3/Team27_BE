package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.REPORT;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.report.ReportCreateRequestDTO;
import com.ktc.togetherPet.model.dto.suspect.ReportDetailResponseDTO;
import com.ktc.togetherPet.model.dto.suspect.ReportNearByResponseDTO;
import com.ktc.togetherPet.model.dto.suspect.SuspectRequestDTO;
import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Report;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.MissingRepository;
import com.ktc.togetherPet.repository.ReportRepository;
import com.ktc.togetherPet.repository.UserRepository;
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
    private final MissingRepository missingRepository;
    private final KakaoMapService kakaoMapService;
    private final ImageService imageService;
    private final UserRepository userRepository;

    @Transactional
    public void createReport(
        ReportCreateRequestDTO reportCreateRequestDTO,
        List<MultipartFile> files,
        OauthUserDTO oauthUserDTO
    ) {
        User user = userRepository.findByEmail(oauthUserDTO.email())
            .orElseThrow(CustomException::invalidUserException);

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
            .ifPresent(missingId -> report.setMissing(
                    missingRepository
                        .findById(missingId)
                        .orElseThrow(CustomException::missingNotFound)
                )
            );

        long reportId = reportRepository.save(report).getId();
        imageService.saveImages(reportId, REPORT, files);
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

    public void setBreed(Long reportId, String breed) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(CustomException::reportNotFoundException);
        Breed breedEntity = new Breed(breed);

        report.setBreed(breedEntity);

        reportRepository.save(report);
    }

    public void setGender(Long reportId, String gender) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(CustomException::reportNotFoundException);

        report.setGender(gender);

        reportRepository.save(report);
    }

    public void setMissing(Long reportId, Long missingId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(CustomException::reportNotFoundException);

        Missing missing = missingRepository.findById(missingId)
            .orElseThrow(CustomException::missingNotFound);

        report.setMissing(missing);

        reportRepository.save(report);
    }
}
