package com.ktc.togetherPet.controller;

import static org.springframework.http.MediaType.IMAGE_JPEG;

import com.ktc.togetherPet.apiResponse.CustomResponse;
import com.ktc.togetherPet.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{file-name}")
    public ResponseEntity<byte[]> getImage(
        @PathVariable("file-name") String fileName
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(IMAGE_JPEG);

        return CustomResponse.ok(headers, imageService.getImageBytesFromFileName(fileName));
    }
}
