package com.ktc.togetherPet.model.vo;

import static com.ktc.togetherPet.exception.CustomException.invalidLocaltionException;

import jakarta.persistence.Embeddable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return Float.compare(latitude, location.latitude) == 0
            && Float.compare(longitude, location.longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
