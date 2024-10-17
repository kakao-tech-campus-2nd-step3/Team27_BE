package com.ktc.togetherPet.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.Report;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.Location;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ReportRepositoryTest {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private BreedRepository breedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private MissingRepository missingRepository;

    @Test
    @DisplayName("실종을 바탕으로 모든 제보 찾기 테스트/findAllByMissing")
    void 실종을_바탕으로_모든_제보_찾기() {
        // given
        Breed givenBreed = new Breed("testPetBreed");

        Pet givenPet = new Pet(
            "testPetName",
            1L,
            givenBreed,
            true
        );

        Missing givenMissing = new Missing(
            givenPet,
            true,
            LocalDateTime.of(2024, 10, 17, 19, 39, 11),
            new Location(15D, 15D),
            1L,
            "testMissingDescription"
        );

        List<User> givenUser = List.of(
            new User("test1@email.com"),
            new User("test2@email.com"),
            new User("test3@email.com")
        );

        List<Report> givenReport = List.of(
            new Report(
                givenUser.get(0),
                LocalDateTime.of(2024, 10, 17, 19, 19, 11),
                new Location(15D, 15D),
                1L,
                "testDescription1"
            ),
            new Report(
                givenUser.get(0),
                LocalDateTime.of(2024, 10, 17, 19, 20, 22),
                new Location(15D, 15D),
                1L,
                "testDescription2"
            ),
            new Report(
                givenUser.get(0),
                LocalDateTime.of(2024, 10, 15, 11, 11, 11),
                new Location(17D, 17D),
                1L,
                "testDescription3"
            ),
            new Report(
                givenUser.get(1),
                LocalDateTime.of(2024, 10, 17, 19, 20, 33),
                new Location(30D, 30D),
                2L,
                "testDescription4"
            ),
            new Report(
                givenUser.get(1),
                LocalDateTime.of(2024, 10, 17, 19, 20, 33),
                new Location(30D, 30D),
                1L,
                "testDescription5"
            )
        );

        givenReport.get(0).setMissing(givenMissing);
        givenReport.get(4).setMissing(givenMissing);

        List<Report> expect = List.of(
            givenReport.get(0),
            givenReport.get(4)
        );

        // when
        breedRepository.save(givenBreed);
        petRepository.save(givenPet);
        userRepository.saveAll(givenUser);
        missingRepository.save(givenMissing);
        reportRepository.saveAll(givenReport);

        // then
        List<Report> actual = reportRepository.findAllByMissing(givenMissing);

        assertEquals(actual, expect);

    }
}