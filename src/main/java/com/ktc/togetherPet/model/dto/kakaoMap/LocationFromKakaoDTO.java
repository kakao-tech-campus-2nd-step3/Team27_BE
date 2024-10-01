package com.ktc.togetherPet.model.dto.kakaoMap;

import static com.ktc.togetherPet.exception.CustomException.invalidLocaltionException;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record LocationFromKakaoDTO(
    MetaDTO meta,
    List<Documents> documents
) {

    public String getAdministrative3rdDepthName() {
        return getAdministrativeDocuments().region3depthName();
    }

    public long getAdministrativeCode() {
        return Integer.parseInt(getAdministrativeDocuments().code());
    }

    private Documents getAdministrativeDocuments() {
        for (Documents docs : documents) {
            if (docs.isRegionTypeH()) {
                return docs;
            }
        }
        throw invalidLocaltionException();
    }
}
