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
@Table(name = "pet")
public class Pet {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth_month", nullable = false)
    private Long birthMonth;

    @ManyToOne(targetEntity = Breed.class)
    @JoinColumn(name = "breed_id", nullable = true)
    private Breed breed;

    @Column(name = "is_neutering", nullable = true)
    private Boolean isNeutering;

    public Pet() {
    }

    public Pet(String name, Long birthMonth, Breed breed, Boolean isNeutering) {
        this.name = name;
        this.birthMonth = birthMonth;
        this.breed = breed;
        this.isNeutering = isNeutering;
    }

}
