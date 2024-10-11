package com.ktc.togetherPet.model.entity;

import static lombok.AccessLevel.PROTECTED;

import com.ktc.togetherPet.model.vo.Location;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "path")
@NoArgsConstructor(access = PROTECTED)
public class Path {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Location location;

    @ManyToOne(targetEntity = Walk.class)
    @JoinColumn(name = "walk_id", nullable = false)
    private Walk walk;

    public Path(
        Location location,
        Walk walk
    ) {
        this.location = location;
        this.walk = walk;
    }
}
