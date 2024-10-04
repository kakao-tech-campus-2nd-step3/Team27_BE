package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.Missing;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissingRepository extends JpaRepository<Missing, Long> {

    List<Missing> findAllByRegionCode(long regionCode);
}
