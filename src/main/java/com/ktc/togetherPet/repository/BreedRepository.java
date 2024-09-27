package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.Breed;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {

    Optional<Breed> findByName(String name);
}
