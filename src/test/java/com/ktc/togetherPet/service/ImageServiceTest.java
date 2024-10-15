package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.MISSING;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.ktc.togetherPet.config.ImageConfig;
import com.ktc.togetherPet.model.entity.Image;
import com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType;
import com.ktc.togetherPet.model.entity.ImageRelation.ImageRelation;
import com.ktc.togetherPet.repository.ImageRelationRepository;
import com.ktc.togetherPet.repository.ImageRepository;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageRelationRepository imageRelationRepository;

    @Mock
    private ImageConfig imageConfig;

    @Mock
    private UUIDGenerator uuidGenerator;

    @InjectMocks
    private ImageService imageService;

    @Test
    @DisplayName("이미지 저장 테스트/saveImages")
    void 이미지_저장(@TempDir Path tempDir) {
        // given
        long entityId = 1L;
        ImageEntityType imageEntityType = MISSING;
        List<MultipartFile> files = List.of(
            new MockMultipartFile(
                "files",
                "testImage1.jpeg",
                IMAGE_JPEG_VALUE,
                "testImage1.jpeg".getBytes()
            ),
            new MockMultipartFile(
                "files",
                "testImage2.jpeg",
                IMAGE_JPEG_VALUE,
                "testImage2.jpeg".getBytes()
            )
        );
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        List<Image> expectImageEntity = List.of(
            new Image(tempDir.toAbsolutePath().toString() + uuid1 + ".jpeg"),
            new Image(tempDir.toAbsolutePath().toString() + uuid2 + ".jpeg")
        );

        List<ImageRelation> saveImageRelation = List.of(
            new ImageRelation(imageEntityType, entityId, expectImageEntity.getFirst()),
            new ImageRelation(imageEntityType, entityId, expectImageEntity.getLast())
        );

        List<ImageRelation> savedImageRelation = List.of(
            new ImageRelation(imageEntityType, entityId, expectImageEntity.getFirst()),
            new ImageRelation(imageEntityType, entityId, expectImageEntity.getLast())
        );

        // when
        when(imageConfig.folderPath())
            .thenReturn(tempDir.toAbsolutePath().toString());

        when(uuidGenerator.generateId(files.getFirst()))
            .thenReturn(uuid1);

        when(uuidGenerator.generateId(files.getLast()))
            .thenReturn(uuid2);

        when(imageRepository.saveAll(expectImageEntity))
            .thenReturn(expectImageEntity);

        when(imageRelationRepository.saveAll(saveImageRelation))
            .thenReturn(savedImageRelation);

        // then
        imageService.saveImages(entityId, imageEntityType, files);

        verify(imageRepository, times(1))
            .saveAll(expectImageEntity);

        verify(imageRelationRepository, times(1))
            .saveAll(saveImageRelation);
    }
}