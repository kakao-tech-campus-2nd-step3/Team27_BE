package com.ktc.togetherPet.model.dto.suspect;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record ReportDetailDTO(
    String petBreed,
    String petColor,
    String petGender,
    float latitude,
    float longitude,
    String description,
    String reporterName,
    List<String> imageUrl,
    LocalDateTime foundDate
) {

}
