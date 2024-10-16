package com.ktc.togetherPet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.ktc.togetherPet.config.RestTemplateConfig;
import com.ktc.togetherPet.config.property.KakaoProperties;
import com.ktc.togetherPet.model.vo.Location;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Import(RestTemplateConfig.class)
@RestClientTest(KakaoMapService.class)
class KakaoMapServiceTest {

    @Autowired
    private KakaoProperties kakaoProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KakaoMapService kakaoMapService;

    @Autowired
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    @DisplayName("카카오 API를 통해 지역 코드 가져오기 테스트/getRegionCodeFromKakao")
    void 카카오로_부터_지역_코드를_가져오기() {
        // given
        Location location = new Location(15.0D, 15.0D);
        URI uri = UriComponentsBuilder
            .fromHttpUrl(kakaoProperties.administrativeUrl())
            .queryParam("x", location.getLongitude())
            .queryParam("y", location.getLatitude())
            .build()
            .toUri();

        String expectResponse = """
            {
              "meta": {
                "total_count": 2
              },
              "documents": [
                {
                  "region_type": "B",
                  "address_name": "경기도 성남시 분당구 삼평동",
                  "region_1depth_name": "경기도",
                  "region_2depth_name": "성남시 분당구",
                  "region_3depth_name": "삼평동",
                  "region_4depth_name": "",
                  "code": "4113510900",
                  "x": 127.10459896729914,
                  "y": 37.40269721785548
                },
                {
                  "region_type": "H",
                  "address_name": "경기도 성남시 분당구 삼평동",
                  "region_1depth_name": "경기도",
                  "region_2depth_name": "성남시 분당구",
                  "region_3depth_name": "삼평동",
                  "region_4depth_name": "",
                  "code": "4113565500",
                  "x": 127.1163593869371,
                  "y": 37.40612091848614
                }
              ]
            }
            """;

        // when
        mockServer.expect(requestTo(uri))
            .andExpect(method(GET))
            .andExpect(header("Authorization", "KakaoAK " + kakaoProperties.clientId()))
            .andExpect(header("Content-Type", APPLICATION_JSON_VALUE))
            .andRespond(withSuccess(expectResponse, APPLICATION_JSON));

        // then
        assertEquals(4113565500L, kakaoMapService.getRegionCodeFromKakao(location));
    }
}