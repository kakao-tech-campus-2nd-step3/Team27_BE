package com.ktc.togetherPet.model.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "walk_statistic")
@NoArgsConstructor(access = PROTECTED)
@Getter
public class WalkStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = Pet.class)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "walk_day", nullable = false)
    private Long walkDay;

    @Column(name = "walk_count", nullable = false)
    private Long walkCount;

    @Column(name = "total_distance", nullable = false)
    private Float totalDistance;

    @Column(name = "total_walk_time", nullable = false)
    private Long totalWalkTime;


    @Column(nullable = false)
    private LocalDate lastUpdatedWalkDate;

    public WalkStatistic(
        Walk walk
    ) {
        this.pet = walk.getPet();
        this.walkDay = 1L;
        this.walkCount = 0L;
        this.totalDistance = 0.0F;
        this.totalWalkTime = 0L;
        this.lastUpdatedWalkDate = LocalDate.now();
    }

    public void updateWalkStatistic(Float distance, Long walkTime) {
        this.walkCount += 1;

        this.totalDistance += distance;
        this.totalWalkTime += walkTime;

        if(this.lastUpdatedWalkDate.isBefore(LocalDate.now())) {
            this.walkDay += 1;
            this.lastUpdatedWalkDate = LocalDate.now();
        }
    }
}
