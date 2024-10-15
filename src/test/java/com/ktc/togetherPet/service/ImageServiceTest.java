package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.exception.ErrorMessage.IMAGE_NOT_FOUND;
import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.MISSING;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.ktc.togetherPet.config.ImageConfig;
import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.entity.Image;
import com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType;
import com.ktc.togetherPet.model.entity.ImageRelation.ImageRelation;
import com.ktc.togetherPet.repository.ImageRelationRepository;
import com.ktc.togetherPet.repository.ImageRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("대표 이미지 경로 가져오기 테스트/getRepresentativeImageById")
    class GetRepresentativeImageById {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            ImageEntityType imageEntityType = MISSING;
            long entityId = 1L;

            ImageRelation expectImageRelation = new ImageRelation(
                imageEntityType, entityId, new Image("tempDir/testImage1.jpeg")
            );

            String expectRepresentativeImageUrl = "https://test-image-source/testImage1.jpeg";

            // when
            when(imageRelationRepository
                .findFirstByImageEntityTypeAndEntityId(imageEntityType, entityId)
            ).thenReturn(Optional.of(expectImageRelation));

            when(imageConfig.sourcePrefix())
                .thenReturn("https://test-image-source/");

            // then
            assertEquals(
                expectRepresentativeImageUrl,
                imageService.getRepresentativeImageById(imageEntityType, entityId)
            );

            verify(imageRelationRepository, times(1))
                .findFirstByImageEntityTypeAndEntityId(imageEntityType, entityId);

            verify(imageConfig, times(1))
                .sourcePrefix();
        }

        @Test
        @DisplayName("이미지가 없는 경우")
        void 이미지가_없는_경우() {
            // given
            ImageEntityType imageEntityType = MISSING;
            long entityId = 1L;

            // when
            when(imageRelationRepository
                .findFirstByImageEntityTypeAndEntityId(imageEntityType, entityId)
            ).thenReturn(Optional.empty());

            // then
            CustomException thrown = assertThrows(
                CustomException.class,
                () -> imageService.getRepresentativeImageById(imageEntityType, entityId)
            );

            assertAll(
                () -> assertEquals(IMAGE_NOT_FOUND, thrown.getErrorMessage()),
                () -> assertEquals(NOT_FOUND, thrown.getStatus())
            );

            verify(imageRelationRepository, times(1))
                .findFirstByImageEntityTypeAndEntityId(imageEntityType, entityId);

            verify(imageConfig, never())
                .sourcePrefix();
        }
    }

    @Test
    @DisplayName("모든 이미지 url 가져오기 테스트/getImageUrl")
    void 모든_이미지_url_가져오기() {
        // given
        ImageEntityType imageEntityType = MISSING;
        long entityId = 1L;

        List<ImageRelation> expectImageRelation = List.of(
            new ImageRelation(imageEntityType, entityId, new Image("tempDir/testImage1.jpeg")),
            new ImageRelation(imageEntityType, entityId, new Image("tempDir/testImage2.jpeg")),
            new ImageRelation(imageEntityType, entityId, new Image("tempDir/testImage3.jpeg"))
        );

        List<String> expectImageUrl = List.of(
            "https://test-image-source/testImage1.jpeg",
            "https://test-image-source/testImage2.jpeg",
            "https://test-image-source/testImage3.jpeg"
        );

        // when
        when(imageRelationRepository
            .findAllByImageEntityTypeAndEntityId(imageEntityType, entityId)
        ).thenReturn(expectImageRelation);

        when(imageConfig.sourcePrefix())
            .thenReturn("https://test-image-source/");

        // then
        assertEquals(expectImageUrl, imageService.getImageUrl(entityId, imageEntityType));

        verify(imageRelationRepository, times(1))
            .findAllByImageEntityTypeAndEntityId(imageEntityType, entityId);

        verify(imageConfig, times(expectImageRelation.size()))
            .sourcePrefix();
    }

    @Test
    @DisplayName("실제 이미지 가져오기 테스트/getImageBytesFromFileName")
    void 실제_이미지_가져오기(@TempDir Path tempDir) throws IOException {
        // given
        String fileName = "testImage1.jpeg";
        Path tempFile = tempDir.resolve(fileName);
        byte[] testImageData = new byte[]{1, 2, 3, 4, 5};
        Files.write(tempFile, testImageData);

        // when

        // 경로만 가져오면 마지막에 /값이 누락되어 있음
        when(imageConfig.folderPath())
            .thenReturn(tempDir.toAbsolutePath() + "/");

        // then
        byte[] actual = imageService.getImageBytesFromFileName(fileName);

        assertAll(
            () -> assertTrue(Files.exists(tempFile)),
            () -> assertArrayEquals(testImageData, actual)
        );
    }
}