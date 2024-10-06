package com.ktc.togetherPet.model.dto.suspect;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record ReportNearByDTO (
    long reportId,
    float latitude,
    float longitude,
    String reportRepImageUrl
) {

}
