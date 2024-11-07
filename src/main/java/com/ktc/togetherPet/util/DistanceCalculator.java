package com.ktc.togetherPet.util;

import com.ktc.togetherPet.model.vo.Location;

public class DistanceCalculator {
    private static final double EARTH_RADIUS = 6371e3;

    public static double calculateDistance(Location previousLocation, Location nowLocation) {
        double lat1 = Math.toRadians(previousLocation.getLatitude());
        double lon1 = Math.toRadians(previousLocation.getLongitude());
        double lat2 = Math.toRadians(nowLocation.getLatitude());
        double lon2 = Math.toRadians(nowLocation.getLongitude());

        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
            Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

}
