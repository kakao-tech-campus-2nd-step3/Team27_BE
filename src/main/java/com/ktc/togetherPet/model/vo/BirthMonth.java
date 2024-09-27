package com.ktc.togetherPet.model.vo;

import static com.ktc.togetherPet.exception.CustomException.invalidPetBirthMonthException;

import jakarta.persistence.Embeddable;

@Embeddable
public class BirthMonth {

    private long birthMonth;

    public BirthMonth() {
    }

    public BirthMonth(long birthMonth) {
        validate(birthMonth);
        this.birthMonth = birthMonth;
    }

    private void validate(long birthMonth) {
        if (birthMonth <= 0) {
            throw invalidPetBirthMonthException();
        }
    }
}
