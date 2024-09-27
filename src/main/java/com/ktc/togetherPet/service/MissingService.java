package com.ktc.togetherPet.service;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.missing.MissingPetDTO;
import com.ktc.togetherPet.model.dto.user.UserDTO;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.BreedRepository;
import com.ktc.togetherPet.repository.MissingRepository;
import com.ktc.togetherPet.repository.PetRepository;
import com.ktc.togetherPet.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MissingService {

    private final MissingRepository missingRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final BreedRepository breedRepository;

    public MissingService(
        MissingRepository missingRepository,
        UserRepository userRepository,
        PetRepository petRepository,
        BreedRepository breedRepository
    ) {
        this.missingRepository = missingRepository;
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.breedRepository = breedRepository;
    }

    public void registerMissingPet(UserDTO userDTO, MissingPetDTO missingPetDTO) {
        // TODO 인증 관련 로직 완성 후 수정 필요
        User user = userRepository.findById(userDTO)
            .orElseThrow(CustomException::invalidUserException);

        Pet pet = Optional.ofNullable(user.getPet())
            .orElse(
                petRepository.save(
                    new Pet(
                        missingPetDTO.petName(),
                        missingPetDTO.petAge(),
                        breedRepository.findByName(missingPetDTO.petBreed())
                            .orElseThrow(CustomException::breedNotFoundException),
                        missingPetDTO.isNeutering()
                    )
                )
            );

        missingRepository.save(
            new Missing(
                pet,
                true,
                missingPetDTO.lostTime(),
                new Location(missingPetDTO.lostLatitude(), missingPetDTO.lostLongitude())
            )
        );
    }
}
