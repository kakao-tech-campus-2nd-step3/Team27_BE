package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.dto.walk.WalkInformationDTO;
import com.ktc.togetherPet.model.entity.Walk;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkRepository extends JpaRepository<Walk, Long> {
    // 날짜별 산책
    @Query("select w from Walk w where w.pet.id = :petId and w.walkDate >= :startOfDay and w.walkDate < :endOfDay")
    List<Walk> getWalksByDate(@Param("petId") Long petId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}

