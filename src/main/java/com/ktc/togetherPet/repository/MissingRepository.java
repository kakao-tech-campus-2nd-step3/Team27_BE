package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissingRepository extends JpaRepository<Missing, Long> {

    // TODO Missing값이 true인 값만 찾아오도록 변경
    List<Missing> findAllByRegionCode(long regionCode);

    Optional<Missing> findByPet(Pet pet);
}
