package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.exception.CustomException.IOException;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.ktc.togetherPet.config.ImageConfig;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageRelationRepository imageRelationRepository;
    private final UUIDGenerator uuidGenerator;
    private final ImageConfig imageConfig;

    public void saveImages(
        long entityId,
        ImageEntityType imageEntityType,
        List<MultipartFile> files
    ) {
        List<String> imagePaths = files
            .stream()
            .map(this::save2LocalDirectory)
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

    private String save2LocalDirectory(MultipartFile file) {
        String path = createStoreFileName(file);
        try {
            file.transferTo(new File(path));
        } catch (IOException e) {
            throw IOException(e);
        }
        return path;
    }

    private String createStoreFileName(MultipartFile file) {
        return imageConfig.folderPath() +
            uuidGenerator.generateId(file) +
            getFileExtension(file.getOriginalFilename());
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    public String getRepresentativeImageById(ImageEntityType entityType, Long id) {
        ImageRelation representationImageRelation = imageRelationRepository
            .findFirstByImageEntityTypeAndEntityId(entityType, id)
            .orElseThrow(CustomException::imageNotFoundException);

        return localImagePath2RemoteImagePath(representationImageRelation.getImage().getPath());
    }

    private String localImagePath2RemoteImagePath(String localImagePath) {
        return imageConfig.sourcePrefix() + new File(localImagePath).getName();
    }

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

    public byte[] getImageBytesFromFileName(String fileName) {
        try {
            return Files.readAllBytes(new File(imageConfig.folderPath() + fileName).toPath());
        } catch (IOException e) {
            throw IOException(e);
        }
    }
}
