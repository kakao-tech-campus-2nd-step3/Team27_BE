package com.ktc.togetherPet.model.entity;

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
import java.util.Objects;

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

    @Embedded
    private DateTime lostTime;

    @Embedded
    private Location location;

    @Column(name = "region_code", nullable = false)
    private long regionCode;

    public Missing() {
    }

    public Missing(
        Pet pet,
        Boolean isMissing,
        DateTime dateTime,
        Location location,
        long regionCode
    ) {
        this.pet = pet;
        this.isMissing = isMissing;
        this.lostTime = dateTime;
        this.location = location;
        this.regionCode = regionCode;
    }

    public Pet getPet() {
        return pet;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Missing missing = (Missing) o;
        return regionCode == missing.regionCode && Objects.equals(id, missing.id)
            && Objects.equals(pet, missing.pet) && Objects.equals(isMissing,
            missing.isMissing) && Objects.equals(lostTime, missing.lostTime)
            && Objects.equals(location, missing.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pet, isMissing, lostTime, location, regionCode);
    }
}
