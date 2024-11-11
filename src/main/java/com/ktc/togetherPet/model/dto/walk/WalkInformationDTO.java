package com.ktc.togetherPet.model.dto.walk;

public record WalkInformationDTO (
    Long todayWalkCount,
    Double averageWalkCount,
    Double todayWalkTime,
    Double averageWalkTime,
    Double todayWalkDistance,
    Double averageWalkDistance
) {
}