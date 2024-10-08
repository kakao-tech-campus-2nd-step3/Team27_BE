package com.ktc.togetherPet.model.vo;

import static com.ktc.togetherPet.exception.CustomException.invalidLocaltionException;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Location {

    private double latitude;
    private double longitude;

    public Location() {
    }

    public Location(double latitude, double longitude) {
        validate(latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private void validate(double latitude, double longitude) {
        if (!validateLatitude(latitude) || !validateLongitude(longitude)) {
            throw invalidLocaltionException();
        }
    }

    private boolean validateLatitude(double latitude) {
        return latitude <= 90 && latitude >= -90;
    }

    private boolean validateLongitude(double longitude) {
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
        return Double.compare(latitude, location.latitude) == 0
            && Double.compare(longitude, location.longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
