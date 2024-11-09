package com.ktc.togetherPet.model.entity;

import static lombok.AccessLevel.PROTECTED;

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

    @Column(name = "lost_time", nullable = false)
    private LocalDateTime lostTime;

    @Getter
    @Embedded
    private Location location;

    @ManyToOne(targetEntity = Region.class)
    @JoinColumn(name = "region", nullable = false)
    private Region region;

    @Getter
    @Column(name = "description", nullable = true)
    private String description;

    public Missing(
        Pet pet,
        Boolean isMissing,
        LocalDateTime lostTime,
        Location location,
        Region region,
        String description
    ) {
        this.pet = pet;
        this.isMissing = isMissing;
        this.lostTime = lostTime;
        this.location = location;
        this.region = region;
        this.description = description;
    }

    public boolean isMissing() {
        return isMissing;
    }

}
