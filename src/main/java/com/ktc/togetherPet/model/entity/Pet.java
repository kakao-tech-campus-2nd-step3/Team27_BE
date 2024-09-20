package com.ktc.togetherPet.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pet")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    @Column(name = "pet_name", nullable = false)
    private String petName;

    @Column(name = "pet_birth_month", nullable = false)
    private Long petBirthMonth;

    @Column(name = "breed_id", nullable = true)
    private Long breedId;

    @Column(name = "is_neutering", nullable = true)
    private Boolean isNeutering;

    public Pet() {}

    public Pet(String petName, Long petBirthMonth, Long breedId, Boolean isNeutering) {
        this.petName = petName;
        this.petBirthMonth = petBirthMonth;
        this.breedId = breedId;
        this.isNeutering = isNeutering;
    }

}
