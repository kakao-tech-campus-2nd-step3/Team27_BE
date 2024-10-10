package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.Walk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkRepository extends JpaRepository<Walk, Long> {

}
