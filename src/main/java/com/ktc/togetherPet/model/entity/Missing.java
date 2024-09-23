package com.ktc.togetherPet.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "missing")
public class Missing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Pet.class)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

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

    public Missing(Pet pet, Boolean isMissing, LocalDateTime timestamp, Float latitude,
        Float longitude) {
        this.pet = pet;
        this.isMissing = isMissing;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
