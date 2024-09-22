package com.ktc.togetherPet.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    public User() {
    }

    public User(Long regionId, Long petId) {
        this.regionId = regionId;
        this.petId = petId;
    }
}
