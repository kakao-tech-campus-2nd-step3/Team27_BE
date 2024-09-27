package com.ktc.togetherPet.service;

import com.ktc.togetherPet.model.dto.user.UserDTO;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
}
