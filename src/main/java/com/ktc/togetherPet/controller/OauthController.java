package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.annotation.OauthUser;
import com.ktc.togetherPet.model.dto.oauth.OauthRegisterDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/login")
public class OauthController {
    private final UserService userService;

    public OauthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{provider}")
    public ResponseEntity<?> handleOauth(@PathVariable String provider, @OauthUser Object oauthDTO) {
        if (oauthDTO instanceof OauthUserDTO) {
            return ResponseEntity.ok().build();
        }

        if (oauthDTO instanceof OauthRegisterDTO) {
            userService.createUser((OauthRegisterDTO) oauthDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
