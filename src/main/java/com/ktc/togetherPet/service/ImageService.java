package com.ktc.togetherPet.service;

import java.io.File;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    private static final String FOLDER_PATH = "C:/petTogether/";

    public String saveImage(Long petId, MultipartFile petImage) throws IOException {
        File directory = new File(FOLDER_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String extension = getFileExtension(petImage.getOriginalFilename());
        String imageFileName = petId + extension; // petId만 사용하여 파일 이름 생성
        String imageFilePath = FOLDER_PATH + imageFileName;

        File file = new File(imageFilePath);
        petImage.transferTo(file);

        return imageFilePath;
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }
}
