package com.ktc.togetherPet.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    public User() {}

    public User(Long regionId, Long petId) {
        this.regionId = regionId;
        this.petId = petId;
    }
}
