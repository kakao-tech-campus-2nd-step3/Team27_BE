package com.ktc.togetherPet.model.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record ReportDetailResponseDTO(
    String petBreed,
    String petColor,
    String petGender,
    double latitude,
    double longitude,
    String description,
    String reporterName,
    List<String> imageUrl,
    @JsonFormat(pattern = "yyyy.MM.dd (E) HH:mm", timezone = "Asia/Seoul")
    LocalDateTime foundDate
) {

}
