package com.ktc.togetherPet.model.entity;

import static lombok.AccessLevel.PROTECTED;

import com.ktc.togetherPet.model.vo.DateTime;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "missing")
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
public class Missing {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @ManyToOne(targetEntity = Pet.class)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "is_missing", nullable = false)
    private Boolean isMissing;

    @Embedded
    private DateTime lostTime;

    @Getter
    @Embedded
    private Location location;

    @Column(name = "region_code", nullable = false)
    private long regionCode;

    @Getter
    @Column(name = "description", nullable = true)
    private String description;

    public Missing(
        Pet pet,
        Boolean isMissing,
        DateTime dateTime,
        Location location,
        long regionCode,
        String description
    ) {
        this.pet = pet;
        this.isMissing = isMissing;
        this.lostTime = dateTime;
        this.location = location;
        this.regionCode = regionCode;
        this.description = description;
    }

    public boolean isMissing() {
        return isMissing;
    }

}
