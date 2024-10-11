package com.ktc.togetherPet.model.dto.report;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record ReportResponseDTO(
    long id,
    double latitude,
    double longitude,
    String imageUrl
) {

}
