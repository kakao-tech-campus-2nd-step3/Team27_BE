package com.ktc.togetherPet.util;

public class WalkCalculator {

    private static final double AVERAGE_CALORIE_PER_KM = 15.0;

    public static long calculateCalorie(float distance) {
        return (long) (distance * AVERAGE_CALORIE_PER_KM);
    }
}
