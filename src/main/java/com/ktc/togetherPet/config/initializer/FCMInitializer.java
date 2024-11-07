package com.ktc.togetherPet.config.initializer;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.ktc.togetherPet.exception.CustomException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class FCMInitializer {
    @Value("${firebase.service.key-path}")
    String fcmKeyPath;

    @PostConstruct
    public void getFcmCredential() {
        try {
            InputStream refreshToken = new ClassPathResource(fcmKeyPath).getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(refreshToken)).build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw CustomException.IOException(e);
        }
    }
}
