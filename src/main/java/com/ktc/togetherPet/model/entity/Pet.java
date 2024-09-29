package com.ktc.togetherPet.model.entity;

import com.ktc.togetherPet.model.vo.BirthMonth;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth_month", nullable = false)
    private BirthMonth birthMonth;

    @ManyToOne(targetEntity = Breed.class)
    @JoinColumn(name = "breed_id", nullable = true)
    private Breed breed;

    @Column(name = "is_neutering", nullable = true)
    private Boolean isNeutering;

    public Pet() {
    }

    public Pet(String name, BirthMonth birthMonth, Breed breed, Boolean isNeutering) {
        this.name = name;
        this.birthMonth = birthMonth;
        this.breed = breed;
        this.isNeutering = isNeutering;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pet pet = (Pet) o;

        return Objects.equals(id, pet.id) && Objects.equals(name, pet.name)
            && Objects.equals(birthMonth, pet.birthMonth) && Objects.equals(breed,
            pet.breed) && Objects.equals(isNeutering, pet.isNeutering);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, birthMonth, breed, isNeutering);
    }
}
