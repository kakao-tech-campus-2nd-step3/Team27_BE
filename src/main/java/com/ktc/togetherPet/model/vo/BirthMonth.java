package com.ktc.togetherPet.model.vo;

import static com.ktc.togetherPet.exception.CustomException.invalidPetBirthMonthException;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class BirthMonth {

    private long birthMonth;

    public BirthMonth() {
    }

    public BirthMonth(long birthMonth) {
        validate(birthMonth);
        this.birthMonth = birthMonth;
    }

    public long getBirthMonth() {
        return birthMonth;
    }

    private void validate(long birthMonth) {
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
        BirthMonth that = (BirthMonth) o;
        return birthMonth == that.birthMonth;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(birthMonth);
    }
}
