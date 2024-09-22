package com.ktc.togetherPet.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "missing")
public class Missing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "is_missing", nullable = false)
    private Boolean isMissing;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "latitude", nullable = false)
    private Float latitude;

    @Column(name = "longitude", nullable = false)
    private Float longitude;

    public Missing() {
    }

    public Missing(Long petId, Boolean isMissing, LocalDateTime timestamp, Float latitude,
        Float longitude) {
        this.petId = petId;
        this.isMissing = isMissing;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
