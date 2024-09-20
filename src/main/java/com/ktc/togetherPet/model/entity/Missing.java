package com.ktc.togetherPet.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "missing")
public class Missing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long missingId;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "is_missing", nullable = false)
    private Boolean isMissing;

    @Column(name = "missing_timestamp", nullable = false)
    private String missingTimestamp;

    @Column(name = "missing_latitude", nullable = false)
    private Float missingLatitude;

    @Column(name = "missing_longitude", nullable = false)
    private Float missingLongitude;

    public Missing() {}

    public Missing(Long petId, Boolean isMissing, String missingTimestamp, Float missingLatitude, Float missingLongitude) {
        this.petId = petId;
        this.isMissing = isMissing;
        this.missingTimestamp = missingTimestamp;
        this.missingLatitude = missingLatitude;
        this.missingLongitude = missingLongitude;
    }
}
