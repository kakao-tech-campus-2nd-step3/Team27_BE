package com.ktc.togetherPet.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(name = "breed_id", nullable = true)
    private Long breedId;

    @Column(name = "is_neutering", nullable = true)
    private Boolean isNeutering;

    public Pet() {
    }

    public Pet(String name, Long birthmonth, Long breedId, Boolean isNeutering) {
        this.name = name;
        this.birthMonth = birthmonth;
        this.breedId = breedId;
        this.isNeutering = isNeutering;
    }

}
