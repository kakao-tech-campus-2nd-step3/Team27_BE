package com.ktc.togetherPet.apiResponse;

import com.ktc.togetherPet.model.dto.oauth.OauthSuccessDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CustomResponse {
    public static ResponseEntity<?> oauthSuccess(OauthSuccessDTO oauthSuccessDTO) {
        return ResponseEntity
            .status(oauthSuccessDTO.status())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + oauthSuccessDTO.jwtToken())
            .build();
    }

    public static <T> ResponseEntity<T> ok(HttpHeaders headers, T body) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(headers)
            .body(body);
    }

    public static <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(body);
    }

    public static ResponseEntity<?> created() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
