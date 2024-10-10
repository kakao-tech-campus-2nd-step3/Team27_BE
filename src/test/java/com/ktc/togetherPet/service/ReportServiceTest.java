package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.exception.ErrorMessage.REPORT_NOT_FOUND;
import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.REPORT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.report.ReportCreateRequestDTO;
import com.ktc.togetherPet.model.dto.report.ReportDetailResponseDTO;
import com.ktc.togetherPet.model.dto.report.ReportResponseDTO;
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
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Test
    @DisplayName("개인이 받은 제보를 반환 테스트/getReceivedReports")
    void 개인이_받은_제보를_반환() {
        // given
        OauthUserDTO oauthUserDTO = new OauthUserDTO("test@email.com");
        User expectUser = new User(oauthUserDTO.email());
        Pet pet = new Pet(
            "testPetName",
            1L,
            new Breed("testPetBreed"),
            true
        );
        expectUser.setPet(pet);
        Missing expectMissing = new Missing(
            pet,
            true,
            LocalDateTime.of(2024, 10, 11, 5, 41, 22),
            new Location(15.0D, 15.0D),
            1L,
            "testDescription"
        );
        List<Report> expectReports = List.of(
            spy(new Report(
                new User("reporter1@email.com"),
                LocalDateTime.of(2024, 10, 11, 6, 4, 11),
                new Location(15.0D, 15.0D),
                1L,
                "testDescription1"
            )),
            spy(new Report(
                new User("reporter2@email.com"),
                LocalDateTime.of(2024, 10, 11, 6, 4, 11),
                new Location(15.0D, 15.0D),
                1L,
                "testDescription2"
            ))
        );

        String expectRepresentativeImage1 = "https://together-pet/images/test-image-1";
        String expectRepresentativeImage2 = "https://together-pet/images/test-image-2";

        List<ReportResponseDTO> expect = List.of(
            new ReportResponseDTO(
                1L,
                15.0D,
                15.0D,
                expectRepresentativeImage1
            ),
            new ReportResponseDTO(
                2L,
                15.0D,
                15.0D,
                expectRepresentativeImage2
            )
        );

        // when
        when(userService.findUserByEmail(oauthUserDTO.email()))
            .thenReturn(expectUser);

        when(missingService.findByPet(pet))
            .thenReturn(expectMissing);

        when(reportRepository.findAllByMissing(expectMissing))
            .thenReturn(expectReports);

        when(expectReports.get(0).getId())
            .thenReturn(1L);

        when(expectReports.get(1).getId())
            .thenReturn(2L);

        when(imageService.getRepresentativeImageById(REPORT, 1L))
            .thenReturn(expectRepresentativeImage1);

        when(imageService.getRepresentativeImageById(REPORT, 2L))
            .thenReturn(expectRepresentativeImage2);

        // then
        List<ReportResponseDTO> actual = reportService.getReceivedReports(oauthUserDTO);

        assertEquals(expect, actual);

        verify(userService, times(1))
            .findUserByEmail(oauthUserDTO.email());

        verify(missingService, times(1))
            .findByPet(pet);

        verify(reportRepository, times(1))
            .findAllByMissing(expectMissing);

        verify(imageService, times(1))
            .getRepresentativeImageById(REPORT, 1L);

        verify(imageService, times(1))
            .getRepresentativeImageById(REPORT, 2L);
    }

    @Test
    @DisplayName("근처 제보 확인 테스트/getReportsByLocation")
    void 근처_제보_확인() {
        //TODO 논의 후 테스트코드 작성
    }

    @Nested
    @DisplayName("제보 상세 확인 테스트/getReportById")
    class 제보_상세_확인_테스트 {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            long reportId = 1L;
            User expectUser = new User("test@email.com");
            expectUser.setName("testName");
            Report expectReport = new Report(
                expectUser,
                LocalDateTime.of(2024, 10, 11, 6, 44, 11),
                new Location(15.0D, 15.0D),
                1L,
                "testDescription"
            );

            List<String> expectImages = List.of(
                "https://together-pet/images/test-image-1.jpeg",
                "https://together-pet/images/test-image-2.jpeg",
                "https://together-pet/images/test-image-3.jpeg"
            );

            ReportDetailResponseDTO expect = new ReportDetailResponseDTO(
                15.0D,
                15.0D,
                "testDescription",
                "testName",
                expectImages,
                LocalDateTime.of(2024, 10, 11, 6, 44, 11)
            );

            // when
            when(reportRepository.findById(reportId))
                .thenReturn(Optional.of(expectReport));

            when(imageService.getImageUrl(reportId, REPORT))
                .thenReturn(expectImages);

            // then
            ReportDetailResponseDTO actual = reportService.getReportById(reportId);

            assertEquals(expect, actual);

            verify(reportRepository, times(1))
                .findById(reportId);

            verify(imageService, times(1))
                .getImageUrl(reportId, REPORT);
        }

        @Test
        @DisplayName("제보 아이디가 존재하지 않는 경우")
        void 제보_아이디가_존재하지_않는_경우() {
            // given
            long reportId = 1L;

            // when
            when(reportRepository.findById(reportId))
                .thenReturn(Optional.empty());

            // then
            CustomException thrown = assertThrows(
                CustomException.class,
                () -> reportService.getReportById(reportId)
            );

            assertAll(
                () -> assertEquals(REPORT_NOT_FOUND, thrown.getErrorMessage()),
                () -> assertEquals(NOT_FOUND, thrown.getStatus())
            );

            verify(reportRepository, times(1))
                .findById(reportId);

            verify(imageService, never())
                .getImageUrl(reportId, REPORT);
        }
    }
}