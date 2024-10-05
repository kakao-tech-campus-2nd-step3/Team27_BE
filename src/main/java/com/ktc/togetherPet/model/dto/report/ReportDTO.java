package com.ktc.togetherPet.model.dto.report;

public record ReportDTO(
    long id,
    double latitude,
    double longitude,
    String imageUrl
) {

}
