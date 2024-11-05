package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.REPORT;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.report.ReportCreateRequestDTO;
import com.ktc.togetherPet.model.dto.report.ReportDetailResponseDTO;
import com.ktc.togetherPet.model.dto.report.ReportResponseDTO;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.Region;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.entity.report.GeneralReport;
import com.ktc.togetherPet.model.entity.report.MissingReport;
import com.ktc.togetherPet.model.entity.report.ReportBase;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.ReportRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final MissingService missingService;
    private final ImageService imageService;
    private final UserService userService;
    private final RegionService regionService;
    private final BreedService breedService;

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

        Region region = regionService.findByLocation(location);

        ReportBase report = Optional.ofNullable(reportCreateRequestDTO.missingId())
            .map(missingId -> (ReportBase) createMissingReport(user, region, location,
                reportCreateRequestDTO))
            .orElseGet(() -> createGeneralReport(user, region, location, reportCreateRequestDTO));

        Optional.ofNullable(reportCreateRequestDTO.breed())
            .ifPresent(breed -> report.setBreed(breedService.findBreedByName(breed)));

        Optional.ofNullable(reportCreateRequestDTO.gender())
            .ifPresent(report::setGender);

        long reportId = reportRepository.save(report).getId();
        imageService.saveImages(reportId, REPORT, files);
    }

    private MissingReport createMissingReport(
        User user,
        Region region,
        Location location,
        ReportCreateRequestDTO reportCreateRequestDTO
    ) {
        return new MissingReport(
            user,
            reportCreateRequestDTO.foundDate(),
            location,
            region,
            reportCreateRequestDTO.description(),
            missingService.findByMissingId(reportCreateRequestDTO.missingId())
        );
    }

    private GeneralReport createGeneralReport(
        User user,
        Region region,
        Location location,
        ReportCreateRequestDTO reportCreateRequestDTO
    ) {
        return new GeneralReport(
            user,
            reportCreateRequestDTO.foundDate(),
            location,
            region,
            reportCreateRequestDTO.description()
        );
    }

    public List<ReportResponseDTO> getReceivedReports(OauthUserDTO oauthUserDTO) {
        User user = userService.findUserByEmail(oauthUserDTO.email());

        Pet pet = user.getPet();

        Missing missing = missingService.findByPet(pet);
        List<MissingReport> reports = reportRepository.findAllByMissing(missing);

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

    public List<ReportResponseDTO> getReportsByLocation(double latitude, double longitude) {
        Location location = new Location(latitude, longitude);
        Region region = regionService.findByLocation(location);

        return reportRepository.findAllByRegion(region)
            .stream()
            .map(report -> new ReportResponseDTO(
                    report.getId(),
                    report.getLocation().getLatitude(),
                    report.getLocation().getLongitude(),
                    imageService.getRepresentativeImageById(REPORT, report.getId())
                )
            )
            .toList();
    }

    public ReportDetailResponseDTO getReportById(long reportId) {
        ReportBase report = reportRepository.findById(reportId)
            .orElseThrow(CustomException::reportNotFoundException);

        Location location = report.getLocation();

        return new ReportDetailResponseDTO(
            location.getLatitude(),
            location.getLongitude(),
            report.getDescription(),
            report.getUser().getName(),
            imageService.getImageUrl(reportId, REPORT),
            report.getTimeStamp()
        );
    }
}
