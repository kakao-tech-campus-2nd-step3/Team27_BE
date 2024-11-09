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

    /** deprecated
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

    **/

    // 날짜별 산책
    @Query("select w from Walk w where w.pet.id = :petId and w.walkDate >= :startOfDay and w.walkDate < :endOfDay")
    List<Walk> getWalksByDate(@Param("petId") Long petId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);


    @Query(value = ""
        + "SELECT " +
            "COALESCE(today.walk_count, 0) AS todayWalkCount, " +
            "COALESCE(averages.avg_walk_count, 0) AS averageWalkCount, " +
            "COALESCE(today.walk_time, 0) AS todayWalkTime, " +
            "COALESCE(averages.avg_walk_time, 0) AS averageWalkTime, " +
            "COALESCE(today.walk_distance, 0) AS todayWalkDistance, " +
            "COALESCE(averages.avg_walk_distance, 0) AS averageWalkDistance " +
        "FROM " +
            "(SELECT " +
                "COUNT(*) AS walk_count, " +
                "SUM(w.walk_time) AS walk_time, " +
                "SUM(w.distance) AS walk_distance " +
             "FROM " +
                "walk w " +
             "WHERE " +
                "w.pet_id = :petId " +
                "AND CAST(w.walk_date AS DATE) = CAST(NOW() AS DATE) " +
            ") AS today, " +
            "(SELECT " +
                "AVG(daily_walks.walk_count) AS avg_walk_count, " +
                "AVG(daily_walks.walk_time) AS avg_walk_time, " +
                "AVG(daily_walks.walk_distance) AS avg_walk_distance " +
            "FROM " +
                "(SELECT " +
                    "CAST(w.walk_date AS DATE) AS walk_date, " +
                    "COUNT(*) AS walk_count, " +
                    "SUM(w.walk_time) AS walk_time, " +
                    "SUM(w.distance) AS walk_distance " +
                "FROM " +
                    "walk w " +
                "WHERE " +
                    "w.pet_id = :petId " +
                "GROUP BY " +
                    "CAST(w.walk_date AS DATE) " +
                ") AS daily_walks " +
            ") AS averages",
        nativeQuery = true)
    WalkInformationDTO getWalkStatistics(@Param("petId") Long petId);

}

