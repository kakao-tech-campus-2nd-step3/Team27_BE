package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.Path;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PathRepository extends JpaRepository<Path, Long> {
    List<Path> findByWalkId(Long walkId);
}
