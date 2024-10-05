package com.ktc.togetherPet.model.vo;

import static com.ktc.togetherPet.exception.CustomException.invalidDateException;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class DateTime {

    private LocalDateTime localDateTime;

    public DateTime() {
    }

    public DateTime(LocalDateTime localDateTime) {
        validate(localDateTime);
        this.localDateTime = localDateTime;
    }

    private void validate(LocalDateTime localDateTime) {
        validateLocalDateTime(localDateTime);
    }

    private void validateLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime.isAfter(LocalDateTime.now())) {
            throw invalidDateException();
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
        DateTime dateTime = (DateTime) o;
        return Objects.equals(localDateTime, dateTime.localDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(localDateTime);
    }
}
