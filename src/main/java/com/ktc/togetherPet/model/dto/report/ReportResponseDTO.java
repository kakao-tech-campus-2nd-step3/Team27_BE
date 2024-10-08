package com.ktc.togetherPet.model.dto.report;

public record ReportResponseDTO(
    long id,
    double latitude,
    double longitude,
    String imageUrl
) {

}
