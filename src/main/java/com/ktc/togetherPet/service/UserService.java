package com.ktc.togetherPet.service;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.user.UserDTO;
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

    public UserDTO findUser(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        return new UserDTO(user);
    }

    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void createUser(String email) {
        User user = new User(email);

        userRepository.save(user);
    }

    public void setUserPet(Long petId, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(CustomException::invalidUserException);
        user.setPet(petService.findPetById(petId));

        userRepository.save(user);
    }

    public void setUserName(String email, String userName) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(CustomException::invalidUserException);
        user.setName(userName);

        userRepository.save(user);
    }
}
