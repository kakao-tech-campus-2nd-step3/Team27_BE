package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.WalkStatistic;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkStatisticRepository extends JpaRepository<WalkStatistic, Long> {

    Optional<WalkStatistic> findByPetId(Long PetId);
}
