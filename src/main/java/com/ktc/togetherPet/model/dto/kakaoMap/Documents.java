package com.ktc.togetherPet.model.dto.kakaoMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
record Documents(
    String regionType,
    String addressName,

    @JsonProperty("region_1depth_name")
    String region1depthName,

    @JsonProperty("region_2depth_name")
    String region2depthName,

    @JsonProperty("region_3depth_name")
    String region3depthName,

    @JsonProperty("region_4depth_name")
    String region4depthName,
    String code,
    double x,
    double y
) {

    public boolean isRegionTypeH() {
        return regionType.equals("H");
    }
}
