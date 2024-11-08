package com.ktc.togetherPet.model.dto.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record UserInfoResponseDTO(
    String userName,
    String petName,
    String petImageUrl,
    long petBirthMonth
) {

}
