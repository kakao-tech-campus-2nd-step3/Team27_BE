package com.ktc.togetherPet.repository;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.MISSING;
import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.PET;
import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.REPORT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ktc.togetherPet.model.entity.Image;
import com.ktc.togetherPet.model.entity.ImageRelation.ImageRelation;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ImageRelationRepositoryTest {

    @Autowired
    private ImageRelationRepository imageRelationRepository;

    @Autowired
    private ImageRepository imageRepository;

    private List<ImageRelation> givenImageRelations;

    @BeforeEach
    public void setup() {
        List<Image> givenImages = List.of(
            new Image("image/dir/missing-1-1.jpeg"),
            new Image("image/dir/missing-1-2.jpeg"),
            new Image("image/dir/missing-2-1.jpeg"),
            new Image("image/dir/pet-2-1.jpeg"),
            new Image("image/dir/pet-2-2.jpeg"),
            new Image("image/dir/report-2-1.jpeg"),
            new Image("image/dir/report-2-2.jpeg"),
            new Image("image/dir/report-3-1.jpeg")
        );

        imageRepository.saveAll(givenImages);

        givenImageRelations = List.of(
            new ImageRelation(MISSING, 1L, givenImages.get(0)),
            new ImageRelation(MISSING, 1L, givenImages.get(1)),
            new ImageRelation(MISSING, 2L, givenImages.get(2)),
            new ImageRelation(PET, 2L, givenImages.get(3)),
            new ImageRelation(PET, 1L, givenImages.get(4)),
            new ImageRelation(REPORT, 2L, givenImages.get(5)),
            new ImageRelation(REPORT, 2L, givenImages.get(6)),
            new ImageRelation(REPORT, 3L, givenImages.get(7))
        );
    }

    @Nested
    @DisplayName("이미지 엔티티 타입과 엔티티 아이디를 통해 모든 이미지 가져오기 테스트/findAllByImageEntityAndEntityId")
    class 이미지_엔티티_타입과_엔티티_아이디를_통해_모든_이미지_가져오기 {

        @Test
        @DisplayName("여러개의 이미지 가져오기")
        void 여러개의_이미지_가져오기() {
            // given
            List<ImageRelation> expectImageRelations = List.of(
                givenImageRelations.get(0),
                givenImageRelations.get(1)
            );

            // when
            imageRelationRepository.saveAll(givenImageRelations);

            // then
            List<ImageRelation> actual = imageRelationRepository
                .findAllByImageEntityTypeAndEntityId(MISSING, 1L);

            assertEquals(expectImageRelations, actual);
        }

        @Test
        @DisplayName("이미지가 한개만 존재하는 경우")
        void 이미지가_한개만_존재하는_경우() {
            // given
            List<ImageRelation> expectImageRelations = List.of(givenImageRelations.get(3));

            // when
            imageRelationRepository.saveAll(givenImageRelations);

            // then
            List<ImageRelation> actual = imageRelationRepository
                .findAllByImageEntityTypeAndEntityId(PET, 2L);

            assertEquals(actual, expectImageRelations);
        }

        @Test
        @DisplayName("이미지가 없는 경우")
        void 이미지가_없는_경우() {
            // when
            imageRelationRepository.saveAll(givenImageRelations);

            // then
            List<ImageRelation> actual = imageRelationRepository
                .findAllByImageEntityTypeAndEntityId(REPORT, 1L);

            assertTrue(actual.isEmpty());
        }
    }

    @Nested
    @DisplayName("한개의 이미지를 찾는 테스트/findFirstByImageEntityTypeAndEntityId")
    class 한개의_이미지를_찾기 {

        @Test
        @DisplayName("이미지가 존재하는 경우")
        void 이미지가_존재하는_경우() {
            // given
            Optional<ImageRelation> expect = Optional.of(givenImageRelations.getFirst());

            // when
            imageRelationRepository.saveAll(givenImageRelations);

            // then
            Optional<ImageRelation> actual = imageRelationRepository
                .findFirstByImageEntityTypeAndEntityId(MISSING, 1L);

            assertAll(
                () -> assertEquals(expect, actual),
                () -> assertTrue(actual.isPresent())
            );
        }

        @Test
        @DisplayName("이미지가 존재하지 않는 경우")
        void 이미지가_존재하지_않는_경우() {
            // when
            imageRelationRepository.saveAll(givenImageRelations);

            // then
            Optional<ImageRelation> actual = imageRelationRepository
                .findFirstByImageEntityTypeAndEntityId(PET, 5L);

            assertTrue(actual.isEmpty());
        }
    }
}