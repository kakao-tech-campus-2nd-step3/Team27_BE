package com.ktc.togetherPet.service;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

import com.ktc.togetherPet.config.property.KakaoProperties;
import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.kakaoMap.LocationFromKakaoResponseDTO;
import com.ktc.togetherPet.model.vo.Location;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class KakaoMapService {

    private final KakaoProperties kakaoProperties;

    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    public long getRegionCodeFromKakao(Location location) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + kakaoProperties.clientId());
        headers.add("Content-Type", "application/json");

        URI uri = UriComponentsBuilder
            .fromHttpUrl(kakaoProperties.administrativeUrl())
            .queryParam("x", location.getLongitude())
            .queryParam("y", location.getLatitude())
            .build()
            .toUri();

        RequestEntity<Void> request = new RequestEntity<>(headers, GET, uri);
        ResponseEntity<LocationFromKakaoResponseDTO> response = restTemplate
            .exchange(request, LocationFromKakaoResponseDTO.class);

        if (response.getStatusCode().isSameCodeAs(OK)) {
            LocationFromKakaoResponseDTO locationFromKakaoDTO = response.getBody();

            assert locationFromKakaoDTO != null;
            return locationFromKakaoDTO.getAdministrativeCode();
        }
        throw CustomException.invalidApiException(
            HttpStatus.resolve(response.getStatusCode().value())
        );
    }
}
