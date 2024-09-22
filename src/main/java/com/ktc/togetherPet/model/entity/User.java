package com.ktc.togetherPet.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Region.class)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    public User() {
    }

    public User(Region region, Long petId) {
        this.region = region;
        this.petId = petId;
    }
}
