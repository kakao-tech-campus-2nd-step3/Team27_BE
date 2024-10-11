package com.ktc.togetherPet.model.dto.pet;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record PetRegisterRequestDTO(
    String petName,
    Integer petBirthMonth,
    String petType,
    Boolean isNeutering,
    String petFeature
) {

}
