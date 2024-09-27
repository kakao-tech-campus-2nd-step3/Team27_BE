package com.ktc.togetherPet.model.dto.missing;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;

@JsonNaming(SnakeCaseStrategy.class)
public record MissingPetDTO(
    String petName,
    String petGender,
    long petAge,
    String petBreed,
    LocalDateTime lostTime,
    float lostLatitude,
    float lostLongitude,
    String petFeatures,
    boolean isNeutering
) {

}
