package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.exception.ErrorMessage.REGION_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.exception.ErrorMessage;
import com.ktc.togetherPet.model.entity.Region;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.RegionRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private KakaoMapService kakaoMapService;

    @InjectMocks
    private RegionService regionService;

    private Location givenLocation;

    @BeforeEach
    void setUp() {
        givenLocation = new Location(15.0, 15.0);
    }

    @Test
    @DisplayName("위치를 기반으로 지역 정보 가져오기 테스트/findByLocation")
    void 위치를_기반으로_지역_정보_가져오기() {
        // given
        Region expectRegion = new Region(
            1L,
            "testProvince",
            "testDistrict",
            "testNeighborhood"
        );

        // when
        when(regionRepository.findById(1L))
            .thenReturn(Optional.of(expectRegion));

        when(kakaoMapService.getRegionCodeFromKakao(givenLocation))
            .thenReturn(1L);

        // then
        Region actual = regionService.findByLocation(givenLocation);

        assertEquals(actual, expectRegion);

        verify(kakaoMapService).getRegionCodeFromKakao(givenLocation);
        verify(regionRepository).findById(1L);
    }

    @Test
    @DisplayName("해당 코드가 존재하지 않는 경우/findByLocation")
    void 해당_코드가_존재하지_않는_경우() {
        // when
        when(regionRepository.findById(1L))
            .thenReturn(Optional.empty());

        when(kakaoMapService.getRegionCodeFromKakao(givenLocation))
            .thenReturn(1L);

        // then
        CustomException thrown = assertThrows(
            CustomException.class,
            () -> regionService.findByLocation(givenLocation)
        );

        assertAll(
            () -> assertEquals(thrown.getStatus(), NOT_FOUND),
            () -> assertEquals(thrown.getErrorMessage(), REGION_NOT_FOUND)
        );

        verify(kakaoMapService).getRegionCodeFromKakao(givenLocation);
        verify(regionRepository).findById(1L);
    }
}