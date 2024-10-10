package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.REPORT;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.report.ReportCreateRequestDTO;
import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.Report;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.MissingRepository;
import com.ktc.togetherPet.repository.ReportRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private MissingRepository missingRepository;

    @Mock
    private MissingService missingService;

    @Mock
    private KakaoMapService kakaoMapService;

    @Mock
    private ImageService imageService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReportService reportService;

    @Test
    @DisplayName("제보 등록 테스트/createReport")
    void 제보_등록() {
        // given
        ReportCreateRequestDTO reportCreateRequestDTO = new ReportCreateRequestDTO(
            "testColor",
            15.0D,
            15.0D,
            LocalDateTime.of(2024, 10, 11, 4, 26, 22),
            "testDescription",
            "testBreed",
            "testGender",
            1L
        );

        List<MultipartFile> files = List.of(
            new MockMultipartFile(
                "testFileName",
                "testFileName.jpeg",
                IMAGE_JPEG_VALUE,
                "testFileName.jpeg".getBytes()
            ),
            new MockMultipartFile(
                "testFileName2",
                "testFileName2.jpeg",
                IMAGE_JPEG_VALUE,
                "testFileName2.jpeg".getBytes()
            )
        );

        OauthUserDTO oauthUserDTO = new OauthUserDTO("test@email.com");

        User expectUser = new User(
            oauthUserDTO.email()
        );

        long expectRegionCode = 1L;

        Missing expectMissing = new Missing(
            new Pet(
                "testPetName",
                1L,
                new Breed("testPetBreed"),
                true
            ),
            true,
            LocalDateTime.of(2024, 10, 11, 4, 39, 11),
            new Location(15.0D, 15.0D),
            expectRegionCode,
            "testDescription"
        );

        Report expectReport = new Report(
            expectUser,
            reportCreateRequestDTO.foundDate(),
            new Location(
                reportCreateRequestDTO.foundLatitude(),
                reportCreateRequestDTO.foundLongitude()
            ),
            expectRegionCode,
            reportCreateRequestDTO.description()
        );

        Report savedReport = spy(expectReport);

        expectReport.setBreed(new Breed(reportCreateRequestDTO.breed()));
        expectReport.setGender(reportCreateRequestDTO.gender());
        expectReport.setMissing(expectMissing);

        // when
        when(userService.findUserByEmail(oauthUserDTO.email()))
            .thenReturn(expectUser);

        when(kakaoMapService.getRegionCodeFromKakao(
                new Location(
                    reportCreateRequestDTO.foundLatitude(),
                    reportCreateRequestDTO.foundLongitude()
                )
            )
        ).thenReturn(expectRegionCode);

        when(missingService.findByMissingId(reportCreateRequestDTO.missingId()))
            .thenReturn(expectMissing);

        when(reportRepository.save(expectReport))
            .thenReturn(savedReport);

        when(savedReport.getId())
            .thenReturn(1L);

        // then
        reportService.createReport(reportCreateRequestDTO, files, oauthUserDTO);

        verify(userService, times(1))
            .findUserByEmail(oauthUserDTO.email());

        verify(kakaoMapService, times(1))
            .getRegionCodeFromKakao(
                new Location(
                    reportCreateRequestDTO.foundLatitude(),
                    reportCreateRequestDTO.foundLongitude()
                )
            );

        verify(missingService, times(1))
            .findByMissingId(reportCreateRequestDTO.missingId());

        verify(reportRepository, times(1))
            .save(expectReport);

        verify(imageService, times(1))
            .saveImages(savedReport.getId(), REPORT, files);
    }
}