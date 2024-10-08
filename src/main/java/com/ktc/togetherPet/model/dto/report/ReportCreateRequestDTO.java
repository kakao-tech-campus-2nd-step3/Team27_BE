package com.ktc.togetherPet.model.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReportCreateRequestDTO(
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
