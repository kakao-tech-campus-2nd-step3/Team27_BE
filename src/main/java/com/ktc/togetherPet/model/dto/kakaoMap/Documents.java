package com.ktc.togetherPet.model.dto.kakaoMap;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
record Documents(
    String regionType,
    String addressName,
    String region1depthName,
    String region2depthName,
    String region3depthName,
    String region4depthName,
    String code,
    double x,
    double y
) {

    public boolean isRegionTypeH() {
        return regionType.equals("H");
    }
}
