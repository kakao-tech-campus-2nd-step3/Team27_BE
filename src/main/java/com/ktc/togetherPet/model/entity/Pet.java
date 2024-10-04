package com.ktc.togetherPet.model.entity;

import static com.ktc.togetherPet.exception.CustomException.invalidPetBirthMonthException;

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
    private long birthMonth;

    @ManyToOne(targetEntity = Breed.class)
    @JoinColumn(name = "breed_id", nullable = true)
    private Breed breed;

    @Column(name = "is_neutering", nullable = true)
    private Boolean isNeutering;

    @Column(name = "image_src", nullable = true)
    private String imageSrc;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getBirthMonth() {
        return birthMonth;
    }

    public Breed getBreed() {
        return breed;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public Pet() {
    }

    public Pet(String name, long birthMonth, Breed breed, Boolean isNeutering) {
        validateBirthMonth(birthMonth);

        this.name = name;
        this.birthMonth = birthMonth;
        this.breed = breed;
        this.isNeutering = isNeutering;
    }

    private void validateBirthMonth(long birthMonth) {
        if (birthMonth <= 0) {
            throw invalidPetBirthMonthException();
        }
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
        return birthMonth == pet.birthMonth
            && Objects.equals(id, pet.id)
            && Objects.equals(name, pet.name)
            && Objects.equals(breed, pet.breed)
            && Objects.equals(isNeutering, pet.isNeutering)
            && Objects.equals(imageSrc, pet.imageSrc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, birthMonth, breed, isNeutering, imageSrc);
    }
}
