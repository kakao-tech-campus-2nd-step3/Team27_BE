package com.ktc.togetherPet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.walk.CalorieResponseDTO;
import com.ktc.togetherPet.model.dto.walk.LocationDTO;
import com.ktc.togetherPet.model.dto.walk.WalkInformationDTO;
import com.ktc.togetherPet.model.dto.walk.WalkPathByDateResponseDTO;
import com.ktc.togetherPet.model.dto.walk.WalkRequestDTO;
import com.ktc.togetherPet.model.dto.walk.WalkResponseDTO;
import com.ktc.togetherPet.model.entity.Path;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.entity.Walk;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.WalkRepository;
import com.ktc.togetherPet.util.WalkCalculator;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WalkServiceTest {

    @Mock
    private WalkRepository walkRepository;

    @Mock
    private UserService userService;

    @Mock
    private PetService petService;

    @Mock
    private PathService pathService;

    @InjectMocks
    private WalkService walkService;

    private Pet givenPet;
    private User givenUser;
    private final LocalDateTime fixedTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        givenPet = new Pet("testPet", 1L, null, true);
        givenUser = new User("test@test.com");
        givenUser.setPet(givenPet);
    }

    @Test
    @DisplayName("산책 생성 테스트/createWalk")
    void 산책_생성() {
        // given
        OauthUserDTO oauthUserDTO = new OauthUserDTO("test@test.com");
        WalkRequestDTO walkRequestDTO = new WalkRequestDTO(
            1000,
            2000,
            List.of()
        );

        when(userService.findUserByEmail(oauthUserDTO.email()))
            .thenReturn(givenUser);

        when(petService.findPetById(givenUser.getPet().getId()))
            .thenReturn(givenPet);

        // when
        CalorieResponseDTO response = walkService.createWalk(oauthUserDTO, walkRequestDTO);

        verify(userService, times(1))
            .findUserByEmail(oauthUserDTO.email());

        verify(petService, times(1))
            .findPetById(givenUser.getPet().getId());

        verify(walkRepository, times(1))
            .save(any(Walk.class));

        verify(pathService, times(1))
            .saveAll(anyList());

        assertEquals(WalkCalculator.calculateCalorie(walkRequestDTO.totalWalkDistance()), response.calorie());
    }

    @Test
    @DisplayName("산책 정보 조회 테스트 - getWalkInformation")
    void getWalkInformationTest() {
        // given
        OauthUserDTO oauthUserDTO = new OauthUserDTO("testUser@example.com");

        WalkInformationDTO walkInformation = mock(WalkInformationDTO.class);
        when(walkInformation.getTodayWalkCount()).thenReturn(2L);
        when(walkInformation.getAverageWalkCount()).thenReturn(1.5);
        when(walkInformation.getTodayWalkTime()).thenReturn(3600.0);
        when(walkInformation.getAverageWalkTime()).thenReturn(3000.0);
        when(walkInformation.getTodayWalkDistance()).thenReturn(1700.0);
        when(walkInformation.getAverageWalkDistance()).thenReturn(1400.0);

        int expectedFlag = WalkCalculator.calculateFlag(walkInformation);

        when(userService.findUserByEmail(oauthUserDTO.email())).thenReturn(givenUser);
        when(walkRepository.getWalkStatistics(givenUser.getPet().getId())).thenReturn(walkInformation);

        // when
        WalkResponseDTO response = walkService.getWalkInformation(oauthUserDTO);

        // then
        verify(userService, times(1)).findUserByEmail(oauthUserDTO.email());
        verify(walkRepository, times(1)).getWalkStatistics(givenUser.getPet().getId());

        assertEquals(expectedFlag, response.flagValue());
        assertEquals(walkInformation.getAverageWalkCount().longValue(), response.avgWalkCount());
        assertEquals(walkInformation.getTodayWalkCount(), response.totalCount());
        assertEquals(walkInformation.getAverageWalkTime().longValue(), response.avgWalkTime());
        assertEquals(walkInformation.getTodayWalkTime().longValue(), response.totalWalkTime());
        assertEquals(walkInformation.getAverageWalkDistance(), response.avgWalkDistance());
        assertEquals(walkInformation.getTodayWalkDistance(), response.totalWalkDistance());
    }

    @Test
    @DisplayName("특정 날짜의 산책 경로 조회 테스트 - getWalkPathByDate")
    void getWalkPathByDateTest() {
        // given
        OauthUserDTO oauthUserDTO = new OauthUserDTO("testUser@example.com");
        LocalDateTime date = LocalDateTime.of(2024, 11, 8, 0, 0);

        Walk walk1 = mock(Walk.class);
        Walk walk2 = mock(Walk.class);
        when(walk1.getId()).thenReturn(1L);
        when(walk2.getId()).thenReturn(2L);
        when(walk1.getDistance()).thenReturn(1500.0F);
        when(walk1.getWalkTime()).thenReturn(3600L);
        when(walk1.getWalkDate()).thenReturn(date);
        when(walk2.getDistance()).thenReturn(2000.0F);
        when(walk2.getWalkTime()).thenReturn(4800L);
        when(walk2.getWalkDate()).thenReturn(date.plusHours(1));

        List<Path> paths1 = List.of(new Path(new Location(15.0, 15.0), walk1));
        List<Path> paths2 = List.of(new Path(new Location(20.0, 20.0), walk2));

        when(userService.findUserByEmail(oauthUserDTO.email())).thenReturn(givenUser);
        when(walkRepository.getWalksByDate(givenUser.getPet().getId(), date.toLocalDate().atStartOfDay(), date.toLocalDate().plusDays(1).atStartOfDay()))
            .thenReturn(List.of(walk1, walk2));
        when(pathService.findPathByWalkId(walk1.getId())).thenReturn(List.of(new LocationDTO(paths1.get(0).getLocation().getLatitude(), paths1.get(0).getLocation().getLongitude())));
        when(pathService.findPathByWalkId(walk2.getId())).thenReturn(List.of(new LocationDTO(paths2.get(0).getLocation().getLatitude(), paths2.get(0).getLocation().getLongitude())));

        // when
        List<WalkPathByDateResponseDTO> response = walkService.getWalkPathByDate(oauthUserDTO, date);

        // then
        verify(userService, times(1)).findUserByEmail(oauthUserDTO.email());
        verify(walkRepository, times(1)).getWalksByDate(givenUser.getPet().getId(), date.toLocalDate().atStartOfDay(), date.toLocalDate().plusDays(1).atStartOfDay());
        verify(pathService).findPathByWalkId(1L);
        verify(pathService).findPathByWalkId(2L);

        assertEquals(2, response.size());
        assertEquals(paths1.stream().map(path -> new LocationDTO(path.getLocation().getLatitude(), path.getLocation().getLatitude())).toList(), response.get(0).locationList());
        assertEquals(walk1.getDistance(), response.get(0).walkDistance());
        assertEquals(walk1.getWalkTime(), response.get(0).walkTime());
        assertEquals(walk1.getWalkDate(), response.get(0).walkStartTimePoint());
        assertEquals(walk1.getWalkDate().plusSeconds(walk1.getWalkTime() / 1000), response.get(0).walkEndTimePoint());
    }

}