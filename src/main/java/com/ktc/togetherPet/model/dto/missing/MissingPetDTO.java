package com.ktc.togetherPet.model.dto.missing;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;

@JsonNaming(SnakeCaseStrategy.class)
public record MissingPetDTO(
    String petName,
    String petGender,
    long birthMonth,
    String petBreed,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul") LocalDateTime lostTime,
    double latitude,
    double longitude,
    String description,
    boolean isNeutering
) {

}
