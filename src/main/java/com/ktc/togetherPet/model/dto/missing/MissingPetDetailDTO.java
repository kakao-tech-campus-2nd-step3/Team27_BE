package com.ktc.togetherPet.model.dto.missing;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record MissingPetDetailDTO(
    String name,
    String breed,
    long birthMonth,
    float latitude,
    float longitude,
    String description,
    List<String> imageUrl
) {

}
