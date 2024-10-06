package com.ktc.togetherPet.service;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.suspect.ReportDetailDTO;
import com.ktc.togetherPet.model.dto.suspect.ReportNearByDTO;
import com.ktc.togetherPet.model.dto.suspect.SuspectRequestDTO;
import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Report;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.MissingRepository;
import com.ktc.togetherPet.repository.ReportRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final MissingRepository missingRepository;
    private final KakaoMapService kakaoMapService;
    private final ImageService imageService;

    public ReportService(ReportRepository reportRepository, MissingRepository missingRepository, KakaoMapService kakaoMapService,
        ImageService imageService) {
        this.reportRepository = reportRepository;
        this.missingRepository = missingRepository;
        this.kakaoMapService = kakaoMapService;
        this.imageService = imageService;
    }

    public Long createReport(User user, SuspectRequestDTO suspectRequestDTO) {

        Location location = new Location(
            suspectRequestDTO.foundLatitude(),
            suspectRequestDTO.foundLongitude()
        );

        Report report = reportRepository.save(
            new Report(
                user,
                suspectRequestDTO.foundDate(),
                location,
                kakaoMapService.getRegionCodeFromKakao(location),
                suspectRequestDTO.description()
            )
        );

        return report.getId();
    }

    public List<ReportNearByDTO> getReportsByLocation(float latitude, float longitude) {
        Location location = new Location(latitude, longitude);
        long regionCode = kakaoMapService.getRegionCodeFromKakao(location);

        /** XXX: 이 부분 missing이 null인 경우에 받을 지 아니면 전체 리포트를 받을 지 상의 필요
            * missing이 null인 것만 받아옴 = 실종신고자에게 제보한 것은 제외함 = findAllByRegionCodeAndMissingNotNull
            * 전체 리포트를 받아옴 = 실종신고자에게 제보한 것도 포함함 = findAllByRegionCode
         **/
        return reportRepository.findAllByRegionCode(regionCode)
            .stream()
            .map(report -> {
                    String representImagePath = imageService.getRepresentativeImageById(ImageEntityType.REPORT, report.getId());
                    return new ReportNearByDTO(
                        report.getId(),
                        report.getLocation().getLatitude(),
                        report.getLocation().getLongitude(),
                        representImagePath
                    );
            })
            .collect(Collectors.toList());

    }

    public ReportDetailDTO getReportById(long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(CustomException::reportNotFoundException);

        Location location = report.getLocation();

        return new ReportDetailDTO(
            report.getBreed().getName(),
            report.getColor(),
            report.getGender(),
            location.getLatitude(),
            location.getLongitude(),
            report.getDescription(),
            report.getUser().getName(),
            imageService.getImageRelationsById(ImageEntityType.REPORT, reportId)
                .stream()
                .map(imageRelation -> imageRelation.getImage().getPath())
                .collect(Collectors.toList()),
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
