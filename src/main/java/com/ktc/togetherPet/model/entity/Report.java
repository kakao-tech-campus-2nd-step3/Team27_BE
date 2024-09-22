package com.ktc.togetherPet.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timeStamp;

    @Column(name = "latitude", nullable = false)
    private Float latitude;

    @Column(name = "longitude", nullable = false)
    private Float longitude;

    @Column(name = "missing_animal_id", nullable = true)
    private Long missingAnimalId;

    public Report() {
    }

    public Report(Long userId, LocalDateTime timestamp, Float latitude, Float longitude,
        Long missingAnimalId) {
        this.userId = userId;
        this.timeStamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.missingAnimalId = missingAnimalId;
    }
}
