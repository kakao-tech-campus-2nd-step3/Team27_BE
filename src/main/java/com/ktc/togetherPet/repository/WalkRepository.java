package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.Walk;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkRepository extends JpaRepository<Walk, Long> {

    // 오늘 산책 횟수
    @Query("select count(w) from Walk w where w.pet.id = :petId and w.walkDate >= :startOfDay and w.walkDate < :endOfDay")
    Long getTodayWalkCount(@Param("petId") Long petId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    // 날짜별 평균 산책 횟수
    @Query("select avg(walkCount) from (select count(w) as walkCount from Walk w where w.pet.id = :petId group by w.walkDate)")
    Optional<Double> getAverageWalkCount(@Param("petId") Long petId);

    // 오늘 산책 시간
    @Query("select sum(w.walkTime) from Walk w where w.pet.id = :petId and w.walkDate >= :startOfDay and w.walkDate < :endOfDay")
    Optional<Double> getTodayWalkTime(@Param("petId") Long petId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    // 날짜별 평균 산책 시간
    @Query("select avg(walkTime) from (select sum(w.walkTime) as walkTime from Walk w where w.pet.id = :petId group by w.walkDate)")
    Optional<Double> getAverageWalkTime(@Param("petId") Long petId);

    // 오늘 산책 거리
    @Query("select sum(w.distance) from Walk w where w.pet.id = :petId and w.walkDate >= :startOfDay and w.walkDate < :endOfDay")
    Optional<Double> getTodayWalkDistance(@Param("petId") Long petId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    // 날짜별 평균 산책 거리
    @Query("select avg(walkDistance) from (select sum(w.distance) as walkDistance from Walk w where w.pet.id = :petId group by w.walkDate)")
    Optional<Double> getAverageWalkDistance(@Param("petId") Long petId);

    List<Walk> findByPet(Pet pet);
}

