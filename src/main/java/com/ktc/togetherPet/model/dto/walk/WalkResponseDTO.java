package com.ktc.togetherPet.model.dto.walk;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record WalkResponseDTO(
    int flagValue,
    Long totalCount,
    Long avgWalkCount,
    Double totalWalkDistance,
    Double avgWalkDistance,
    Long totalWalkTime,
    Long avgWalkTime
) {

    public WalkResponseDTO(int flagValue, WalkInformationDTO walkInformationDTO) {
        this(
            flagValue,
            walkInformationDTO.getTodayWalkCount(),
            walkInformationDTO.getAverageWalkCount().longValue(),
            walkInformationDTO.getTodayWalkDistance(),
            walkInformationDTO.getAverageWalkDistance(),
            walkInformationDTO.getTodayWalkTime().longValue(),
            walkInformationDTO.getAverageWalkTime().longValue()
        );
    }
}
