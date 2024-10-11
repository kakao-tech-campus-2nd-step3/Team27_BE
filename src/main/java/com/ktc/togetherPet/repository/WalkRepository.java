package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.Walk;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkRepository extends JpaRepository<Walk, Long> {

    List<Walk> findByPet(Pet pet);
}
