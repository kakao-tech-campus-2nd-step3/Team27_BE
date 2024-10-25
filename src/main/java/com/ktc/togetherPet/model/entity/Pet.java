package com.ktc.togetherPet.model.entity;

import static com.ktc.togetherPet.exception.CustomException.invalidPetBirthMonthException;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pet")
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
public class Pet {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(name = "name", nullable = false)
    private String name;

    @Getter
    @Column(name = "birth_month", nullable = false)
    private long birthMonth;

    @Getter
    @ManyToOne(targetEntity = Breed.class)
    @JoinColumn(name = "breed_id", nullable = true)
    private Breed breed;

    @Column(name = "is_neutering", nullable = true)
    private Boolean isNeutering;

    @Setter
    @Column(name = "image_src", nullable = true)
    private String imageSrc;

    public Pet(
        String name,
        long birthMonth,
        Breed breed,
        Boolean isNeutering
    ) {
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
}
