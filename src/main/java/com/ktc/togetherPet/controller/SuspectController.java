package com.ktc.togetherPet.controller;

import com.ktc.togetherPet.model.dto.suspect.SuspectRequestDTO;
import com.ktc.togetherPet.service.SuspectService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/missing/suspect")
public class SuspectController {

    private final SuspectService suspectService;

    public SuspectController(SuspectService suspectService) {
        this.suspectService = suspectService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createSuspect(
        @RequestPart SuspectRequestDTO suspectRequestDTO,
        @RequestPart(required = false) List<MultipartFile> files
        ) {
        /** todo: MultipartFile에 대한 통일성을 지켜야 함. 이 부분 팀원들에게 고지
         */
        suspectService.createMissing(suspectRequestDTO, files);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
