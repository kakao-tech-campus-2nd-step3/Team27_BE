package com.ktc.togetherPet.model.vo;

import static com.ktc.togetherPet.exception.CustomException.invalidLocaltionException;

import jakarta.persistence.Embeddable;

@Embeddable
public class Location {

    private float latitude;
    private float longitude;

    public Location() {
    }

    public Location(float latitude, float longitude) {
        validate(latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void validate(float latitude, float longitude) {
        if (!validateLatitude(latitude) || !validateLongitude(longitude)) {
            throw invalidLocaltionException();
        }
    }

    private boolean validateLatitude(float latitude) {
        return latitude <= 90 && latitude >= -90;
    }

    private boolean validateLongitude(float longitude) {
        return longitude <= 180 && longitude >= -180;
    }
}
