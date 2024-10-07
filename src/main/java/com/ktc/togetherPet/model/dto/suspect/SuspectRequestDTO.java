package com.ktc.togetherPet.model.dto.suspect;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@JsonNaming(SnakeCaseStrategy.class)
public record SuspectRequestDTO(
    @NotNull
    String color,
    @NotNull
    Float foundLatitude,
    @NotNull
    Float foundLongitude,
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime foundDate,
    @NotNull
    String description,

    String breed,
    String gender,
    Long missingId
) {

}
