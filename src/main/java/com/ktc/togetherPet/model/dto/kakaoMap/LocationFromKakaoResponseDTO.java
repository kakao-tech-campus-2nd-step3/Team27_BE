package com.ktc.togetherPet.model.dto.kakaoMap;

import static com.ktc.togetherPet.exception.CustomException.invalidLocationException;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record LocationFromKakaoResponseDTO(
    MetaDTO meta,
    List<Documents> documents
) {
    public long getLegalCode() {
        return Long.parseLong(getLegalDocuments().code());
    }

    private Documents getLegalDocuments() {
        for (Documents docs : documents) {
            if (docs.isRegionTypeB()) {
                return docs;
            }
        }
        throw invalidLocationException();
    }
}
