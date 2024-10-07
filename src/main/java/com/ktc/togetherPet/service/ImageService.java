package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.exception.CustomException.IOException;
import static java.util.UUID.randomUUID;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.entity.Image;
import com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType;
import com.ktc.togetherPet.model.entity.ImageRelation.ImageRelation;
import com.ktc.togetherPet.repository.ImageRelationRepository;
import com.ktc.togetherPet.repository.ImageRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageRelationRepository imageRelationRepository;

    public ImageService(
        ImageRepository imageRepository,
        ImageRelationRepository imageRelationRepository
    ) {
        this.imageRepository = imageRepository;
        this.imageRelationRepository = imageRelationRepository;
    }

    @Value("${image-folder-path}")
    private String FOLDER_PATH;

    public void saveImages(
        long entityId,
        ImageEntityType imageEntityType,
        List<MultipartFile> files
    ) {
        List<String> imagePaths = files
            .stream()
            .map(this::saveFileAndGetPath)
            .toList();

        List<Image> images = imageRepository.saveAll(
            imagePaths.stream()
                .map(Image::new)
                .toList()
        );

        imageRelationRepository.saveAll(
            images.stream()
                .map(image -> new ImageRelation(
                    imageEntityType,
                    entityId,
                    image
                ))
                .toList()
        );
    }

    private String saveFileAndGetPath(MultipartFile file) {
        String path = createStoreFileName(file);
        try {
            file.transferTo(new File(path));
        } catch (IOException e) {
            throw IOException(e);
        }
        return path;
    }

    private String createStoreFileName(MultipartFile file) {
        return FOLDER_PATH + randomUUID() + getFileExtension(file.getOriginalFilename());
    }

    public String getRepresentativeImageById(ImageEntityType entityType, Long id) {
        ImageRelation representationImageRelation = imageRelationRepository
            .findFirstByImageEntityTypeAndEntityId(entityType, id)
            .orElseThrow(CustomException::imageNotFoundException);

        String localImagePath = new File(
            representationImageRelation.getImage().getPath()).getName();

        return localImagePath2RemoteImagePath(localImagePath);
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    // 실제 파일이 있는 경로가 아닌, 서버의 경로를 통해 이미지를 받아올 수 있는 경로를 반환함
    public List<String> getImageUrl(
        long entityId,
        ImageEntityType imageEntityType
    ) {
        return imageRelationRepository
            .findAllByImageEntityTypeAndEntityId(imageEntityType, entityId)
            .stream()
            .map(ImageRelation::getImage)
            .map(Image::getPath)
            .map(this::localImagePath2RemoteImagePath)
            .toList();
    }

    private String localImagePath2RemoteImagePath(String localImagePath) {
        // TODO profile별로 실행 환경을 다르게 해서 수정해야함
        // 현재는 로컬 환경을 기준으로 작성했음
        String IMAGE_SOURCE_PREFIX = "http://localhost:8080/image/";

        return IMAGE_SOURCE_PREFIX + new File(localImagePath).getName();
    }

    public byte[] getImageBytesFromFileName(String fileName) {
        try {
            return Files.readAllBytes(
                new File(FOLDER_PATH + fileName).toPath());
        } catch (IOException e) {
            throw IOException(e);
        }
    }
}
