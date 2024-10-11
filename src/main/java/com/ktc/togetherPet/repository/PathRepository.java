package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.Path;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PathRepository extends JpaRepository<Path, Long> {

}
