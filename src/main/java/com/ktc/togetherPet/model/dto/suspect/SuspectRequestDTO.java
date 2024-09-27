package com.ktc.togetherPet.model.dto.suspect;

import jakarta.validation.constraints.NotNull;

public record SuspectRequestDTO(

    @NotNull
    String species,
    @NotNull
    String color,
    @NotNull
    Float found_latitude,
    @NotNull
    Float found_longitude,

    String breed,
    String gender
) {

}
