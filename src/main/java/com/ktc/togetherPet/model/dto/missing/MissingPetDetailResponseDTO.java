package com.ktc.togetherPet.model.dto.missing;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record MissingPetDetailResponseDTO(
    String name,
    String breed,
    long birthMonth,
    double latitude,
    double longitude,
    String description,
    List<String> imageUrl
) {

}
