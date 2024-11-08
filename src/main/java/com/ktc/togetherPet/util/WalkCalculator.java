package com.ktc.togetherPet.util;

import com.ktc.togetherPet.model.dto.walk.WalkInformationDTO;

public class WalkCalculator {

    private static final double AVERAGE_CALORIE_PER_KM = 15.0;

    public static long calculateCalorie(float distance) {
        return (long) (distance * AVERAGE_CALORIE_PER_KM);
    }

    public static int calculateFlag(WalkInformationDTO walkInformationDTO) {

        int flagValue = 0;

        if(walkInformationDTO.getTodayWalkCount() < walkInformationDTO.getAverageWalkCount()) {
            flagValue += 1;
        }
        if(walkInformationDTO.getTodayWalkTime() < walkInformationDTO.getAverageWalkTime()) {
            flagValue += 1;
        }
        if(walkInformationDTO.getTodayWalkDistance() < walkInformationDTO.getAverageWalkDistance()) {
            flagValue += 1;
        }

        return flagValue;
    }
}
