package com.ktc.togetherPet.service;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.repository.BreedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BreedService {

    private final BreedRepository breedRepository;

    public Breed findBreedByName(String name) {
        return breedRepository.findByName(name)
            .orElseThrow(CustomException::breedNotFoundException);
    }
}
