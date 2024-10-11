package com.ktc.togetherPet.model.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@JsonNaming(SnakeCaseStrategy.class)
public record ReportCreateRequestDTO(
    @NotNull
    String color,
    @NotNull
    double foundLatitude,
    @NotNull
    double foundLongitude,
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
