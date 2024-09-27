package com.ktc.togetherPet.model.dto.missing;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ktc.togetherPet.model.vo.BirthMonth;
import com.ktc.togetherPet.model.vo.Location;
import java.time.LocalDateTime;

@JsonNaming(SnakeCaseStrategy.class)
public record MissingPetDTO(
    String petName,
    String petGender,
    BirthMonth birthMonth,
    String petBreed,
    LocalDateTime lostTime,
    Location location,
    String petFeatures,
    boolean isNeutering
) {

}
