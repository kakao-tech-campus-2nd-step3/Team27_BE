package com.ktc.togetherPet.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.ktc.togetherPet.model.dto.walk.WalkInformationDTO;
import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.Walk;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.jpa.properties.hibernate.globally_quoted_identifiers=false")
class WalkRepositoryTest {

    @Autowired
    private WalkRepository walkRepository;
    @Autowired
    private BreedRepository breedRepository;
    @Autowired
    private PetRepository petRepository;

    private List<Pet> givenPet;
    private List<Walk> givenWalk;
    @BeforeEach
    void setUp() {
        List<Breed> givenBreed = List.of (
            new Breed("breedTest1"),
            new Breed("breedTest2")
        );

        breedRepository.saveAll(givenBreed);

        givenPet = List.of(
            new Pet(
                "petTest1",
                1L,
                givenBreed.get(0),
                true
            ),
            new Pet(
                "petTest2",
                2L,
                givenBreed.get(1),
                true
            ),
            new Pet(
                "petTest3",
                3L,
                givenBreed.get(0),
                false
            ),
            new Pet(
                "petTest4",
                4L,
                givenBreed.get(1),
                false
            )
        );

        petRepository.saveAll(givenPet);

        givenWalk = List.of(
            new Walk(
                givenPet.get(0),
                1123.0F,
                LocalDateTime.of(2024, 11, 8, 10, 11,24),
                1324
            ),
            new Walk(
                givenPet.get(0),
                1234.0F,
                LocalDateTime.of(2024, 11, 8, 18, 11,24),
                1324
            ),
            new Walk(
                givenPet.get(0),
                1234.0F,
                LocalDateTime.of(2024, 11, 9, 12, 11,24),
                1324
            ),
            new Walk(
                givenPet.get(1),
                1234.0F,
                LocalDateTime.of(2024, 11, 8, 12, 11,24),
                1324
            ),
            new Walk(
                givenPet.get(1),
                1234.0F,
                LocalDateTime.of(2024, 11, 8, 13, 11,24),
                1324
            ),
            new Walk(
                givenPet.get(2),
                1234.0F,
                LocalDateTime.of(2024, 11, 9, 12, 11,24),
                1324
            ),
            new Walk(
                givenPet.get(3),
                1234.0F,
                LocalDateTime.of(2024, 11, 9, 13, 11,24),
                1324
            )
        );
    }

    @Test
    @DisplayName("선택한 날짜별 산책 데이터 가져오기 테스트 수행")
    void 날짜_기반으로_산책_데이터_가져오기() {
        // given
        walkRepository.saveAll(givenWalk);

        for(Pet pet : givenPet) {
            // 리스트중 모든 날짜 Set
            Set<LocalDate> uniqueDates = givenWalk.stream()
                .map(walk -> walk.getWalkDate().toLocalDate())
                .collect(Collectors.toSet());

            // 각 날짜에 테스트 수행
            for (LocalDate date : uniqueDates) {
                // 해당 날짜의 범위
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = date.plusDays(1).atStartOfDay();

                // 기대되는 데이터 추출
                List<Walk> expect = givenWalk.stream()
                    .filter(walk -> walk.getPet().equals(pet) && walk.getWalkDate().toLocalDate().equals(date))
                    .collect(Collectors.toList());

                // when
                List<Walk> actual = walkRepository.getWalksByDate(pet.getId(), start, end);

                // then
                assertEquals(expect, actual);
            }
        }
    }

    @Test
    @DisplayName("펫 정보 기반으로 평균 통계 객체 받아오기 테스트/getWalkStatistics")
    void 펫_정보_기반으로_평균_통계_객체_받아오기()  {
        // given
        walkRepository.saveAll(givenWalk);
        LocalDate today = LocalDate.of(2024, 11, 8);

        for (Pet pet : givenPet) {
            // 필터링하여 해당 펫의 산책 데이터만 추출
            List<Walk> petWalks = givenWalk.stream()
                .filter(walk -> walk.getPet().equals(pet))
                .collect(Collectors.toList());

            // 오늘 날짜의 데이터와 모든 날짜별 데이터로 분류
            List<Walk> todayWalks = petWalks.stream()
                .filter(walk -> walk.getWalkDate().toLocalDate().equals(today))
                .collect(Collectors.toList());

            Map<LocalDate, List<Walk>> dailyWalksMap = petWalks.stream()
                .collect(Collectors.groupingBy(walk -> walk.getWalkDate().toLocalDate()));

            // 예상 값 계산
            long expectedTodayWalkCount = todayWalks.size();
            double expectedTodayWalkTime = todayWalks.stream()
                .mapToDouble(Walk::getWalkTime)
                .sum();
            double expectedTodayWalkDistance = todayWalks.stream()
                .mapToDouble(Walk::getDistance)
                .sum();

            double expectedAverageWalkCount = dailyWalksMap.values().stream()
                .mapToInt(List::size)
                .average()
                .orElse(0.0);
            double expectedAverageWalkTime = dailyWalksMap.values().stream()
                .mapToDouble(dayWalks -> dayWalks.stream().mapToDouble(Walk::getWalkTime).sum())
                .average()
                .orElse(0.0);
            double expectedAverageWalkDistance = dailyWalksMap.values().stream()
                .mapToDouble(dayWalks -> dayWalks.stream().mapToDouble(Walk::getDistance).sum())
                .average()
                .orElse(0.0);

            // when
            WalkInformationDTO actual = walkRepository.getWalkStatistics(pet.getId());

            // then
            assertNotNull(actual, pet.getName());
            assertEquals(expectedTodayWalkCount, actual.getTodayWalkCount(), pet.getName());
            assertEquals(expectedAverageWalkCount, actual.getAverageWalkCount(), 0.01, pet.getName());
            assertEquals(expectedTodayWalkTime, actual.getTodayWalkTime(), 0.01, pet.getName());
            assertEquals(expectedAverageWalkTime, actual.getAverageWalkTime(), 0.01, pet.getName());
            assertEquals(expectedTodayWalkDistance, actual.getTodayWalkDistance(), 0.01, pet.getName());
            assertEquals(expectedAverageWalkDistance, actual.getAverageWalkDistance(), 0.01, pet.getName());
        }
    }
}