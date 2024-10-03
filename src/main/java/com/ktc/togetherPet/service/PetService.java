package com.ktc.togetherPet.service;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.pet.PetRegisterDTO;
import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.repository.PetRepository;
import org.springframework.stereotype.Service;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet findPetById(Long petId) {
        return petRepository.findById(petId).orElseThrow(CustomException::petNotFoundException);
    }

    public Long createPet(PetRegisterDTO petRegisterDTO) {
        Breed breed = new Breed(petRegisterDTO.petType());

        Pet pet = new Pet(petRegisterDTO.petName(), petRegisterDTO.petBirthMonth(),
            breed, petRegisterDTO.isNeutering());
        Pet savedPet = petRepository.save(pet);

        return savedPet.getId();
    }

    public void setImageSrc(Long petId, String imageSrc) {
        Pet pet = petRepository.findById(petId).orElseThrow(CustomException::petNotFoundException);
        pet.setImageSrc(imageSrc);

        petRepository.save(pet);
    }
}
