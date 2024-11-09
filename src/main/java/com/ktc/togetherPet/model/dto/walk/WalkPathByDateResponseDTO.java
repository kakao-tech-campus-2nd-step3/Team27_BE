package com.ktc.togetherPet.model.dto.walk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record WalkPathByDateResponseDTO(
    List<LocationDTO> locationList,
    Float walkDistance,
    Long walkTime,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime walkStartTimePoint,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime walkEndTimePoint
) {

}
