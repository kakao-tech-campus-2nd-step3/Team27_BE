package com.ktc.togetherPet.handler;

import static com.ktc.togetherPet.config.property.FCMProperties.missingBody;
import static com.ktc.togetherPet.config.property.FCMProperties.missingTile;
import static com.ktc.togetherPet.exception.CustomException.invalidTokenException;
import static com.ktc.togetherPet.exception.CustomException.jsonProcessingException;
import static com.ktc.togetherPet.util.DistanceCalculator.calculateDistance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.service.FCMService;
import com.ktc.togetherPet.service.KakaoMapService;
import com.ktc.togetherPet.service.MissingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
@Component
public class LocationWebSocketHandler extends TextWebSocketHandler {
    private static final double NOTIFICATION_DISTANCE_THRESHOLD = 1000.0;

    private final ObjectMapper objectMapper;
    private final MissingService missingService;
    private final FCMService fcmService;
    private final KakaoMapService kakaoMapService;

    private Location previousLocation = new Location(0, 0);
    private long previousRegionCode = -1;
    private double distance = 0;

    private String fcmToken;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.fcmToken = session.getHandshakeHeaders().getFirst("FCM-Token");

        if (fcmToken == null || fcmToken.isEmpty()) {
            throw invalidTokenException();
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        Location nowLocation;

        try {
            nowLocation = objectMapper.readValue(payload, Location.class);
        } catch (JsonProcessingException e) {
            throw jsonProcessingException(e);
        }

        updateDistanceAndLocation(nowLocation);
    }

    private void updateDistanceAndLocation(Location nowLocation) {
        distance += calculateDistance(previousLocation, nowLocation);
        previousLocation = nowLocation;

        if (isOverDistance(distance)) {
            distance = 0;
            processRegionChange(nowLocation);
        }
    }

    private boolean isOverDistance(double distance) {
        return distance > NOTIFICATION_DISTANCE_THRESHOLD;
    }

    private void processRegionChange(Location nowLocation) {
        if (hasRegionCodeChanged(nowLocation) && hasMissingReports()) {
            fcmService.sendMessage(missingTile, missingBody, fcmToken);
        }
    }

    private boolean hasRegionCodeChanged(Location nowLocation) {
        long nowRegionCode = kakaoMapService.getRegionCodeFromKakao(nowLocation);

        if (previousRegionCode != nowRegionCode) {
            previousRegionCode = nowRegionCode;
            return true;
        }
        return false;
    }

    private boolean hasMissingReports() {
        return missingService.countByRegionCode(previousRegionCode) > 0;
    }
}
