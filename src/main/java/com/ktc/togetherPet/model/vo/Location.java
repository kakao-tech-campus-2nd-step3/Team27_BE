package com.ktc.togetherPet.model.vo;

import static com.ktc.togetherPet.exception.CustomException.invalidLocationException;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
public class Location {

    private double latitude;

    private double longitude;

    public Location(double latitude, double longitude) {
        validate(latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void validate(double latitude, double longitude) {
        if (!validateLatitude(latitude) || !validateLongitude(longitude)) {
            throw invalidLocationException();
        }
    }

    private boolean validateLatitude(double latitude) {
        return latitude <= 90 && latitude >= -90;
    }

    private boolean validateLongitude(double longitude) {
        return longitude <= 180 && longitude >= -180;
    }
}
