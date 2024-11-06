package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.PET;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.user.UserInfoResponseDTO;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PetService petService;
    private final ImageService imageService;

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(CustomException::invalidUserException);
    }

    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void createUser(String email) {
        User user = new User(email);

        userRepository.save(user);
    }

    public void setUserPet(Long petId, String email) {
        User user = findUserByEmail(email);
        user.setPet(petService.findPetById(petId));

        userRepository.save(user);
    }

    public void setUserName(String email, String userName) {
        User user = findUserByEmail(email);
        user.setName(userName);

        userRepository.save(user);
    }

    public UserInfoResponseDTO getUserInfo(OauthUserDTO oauthUserDTO) {
        User user = findUserByEmail(oauthUserDTO.email());
        Pet pet = user.getPet();
        return new UserInfoResponseDTO(
            user.getName(),
            pet.getName(),
            imageService.getRepresentativeImageById(PET, pet.getId()),
            pet.getBirthMonth()
        );
    }
}
