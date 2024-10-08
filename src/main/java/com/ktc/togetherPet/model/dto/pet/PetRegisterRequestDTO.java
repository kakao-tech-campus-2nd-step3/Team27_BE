package com.ktc.togetherPet.model.dto.pet;

public record PetRegisterRequestDTO(String petName,
                                    Integer petBirthMonth,
                                    String petType,
                                    Boolean isNeutering, String petFeature) {

}
