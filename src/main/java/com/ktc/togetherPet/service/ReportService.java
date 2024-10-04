package com.ktc.togetherPet.service;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.suspect.SuspectRequestDTO;
import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Report;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.MissingRepository;
import com.ktc.togetherPet.repository.ReportRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final MissingRepository missingRepository;

    public ReportService(ReportRepository reportRepository, MissingRepository missingRepository) {
        this.reportRepository = reportRepository;
        this.missingRepository = missingRepository;
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
                location
            )
        );

        return report.getId();
    }

    public void setBreed(Long reportId, String breed) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(CustomException::reportNotFoundException);
        Breed breedEntity = new Breed(breed);

        report.setBreed(breedEntity);

        reportRepository.save(report);
    }

    public void setColor(Long reportId, String color) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(CustomException::reportNotFoundException);

        report.setColor(color);

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
