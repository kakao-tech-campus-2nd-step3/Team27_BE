package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.Missing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissingRepository extends JpaRepository<Missing, Long> {

}
