package com.ktc.togetherPet.model.entity;

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

    @ManyToOne(targetEntity = Pet.class)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    public User() {
    }

    public User(Region region, Pet pet) {
        this.region = region;
        this.pet = pet;
    }
}
