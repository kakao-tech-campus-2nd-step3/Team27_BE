package com.ktc.togetherPet.model.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "walk")
@NoArgsConstructor(access = PROTECTED)
public class Walk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Pet.class)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(nullable = false)
    private Float distance;

    @Column(nullable = false)
    private LocalDateTime walkDate;

    @Column(nullable = false)
    private long walkTime;

    public Walk(
        Pet pet,
        Float distance,
        LocalDateTime walkDate,
        long walkTime
    ) {
        this.pet = pet;
        this.distance = distance;
        this.walkDate = walkDate;
        this.walkTime = walkTime;
    }
}
