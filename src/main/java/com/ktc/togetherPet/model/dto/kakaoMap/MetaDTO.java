package com.ktc.togetherPet.model.dto.kakaoMap;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
record MetaDTO(
    int totalCount
) {

}
