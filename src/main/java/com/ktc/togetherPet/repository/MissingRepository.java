package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.Region;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissingRepository extends JpaRepository<Missing, Long> {

    List<Missing> findAllByRegionAndIsMissingIsTrue(Region region);

    Optional<Missing> findByPetAndIsMissingIsTrue(Pet pet);

    long countByRegionCodeAndIsMissingTrue(long regionCode);
}
