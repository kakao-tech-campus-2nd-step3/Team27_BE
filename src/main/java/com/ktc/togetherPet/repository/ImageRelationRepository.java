package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType;
import com.ktc.togetherPet.model.entity.ImageRelation.ImageRelation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRelationRepository extends JpaRepository<ImageRelation, Long> {

    List<ImageRelation> findAllByImageEntityTypeAndEntityId(
        ImageEntityType imageEntityType,
        long entityId
    );

    Optional<ImageRelation> findFirstByImageEntityTypeAndEntityId(
        ImageEntityType imageEntityType,
        long entityId
    );
}
