package com.ktc.togetherPet.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @ManyToOne(targetEntity = Region.class)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @ManyToOne(targetEntity = Pet.class)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    public User() {
    }

    public User(String email) {
        this.email = email;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
