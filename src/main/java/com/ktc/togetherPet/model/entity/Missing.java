package com.ktc.togetherPet.model.entity;

import com.ktc.togetherPet.model.vo.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

    @Embedded
    private Location location;

    public Missing() {
    }

    public Missing(Pet pet, Boolean isMissing, LocalDateTime timestamp, Location location) {
        this.pet = pet;
        this.isMissing = isMissing;
        this.timestamp = timestamp;
        this.location = location;
    }
}
