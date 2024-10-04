package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.ImageRelation.ImageRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRelationRepository extends JpaRepository<ImageRelation, Long> {

}
