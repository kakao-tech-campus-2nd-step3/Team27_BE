package com.ktc.togetherPet.repository;

import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.report.GeneralReport;
import com.ktc.togetherPet.model.entity.report.MissingReport;
import com.ktc.togetherPet.model.entity.report.ReportBase;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<ReportBase, Long> {

    @Query("SELECT r FROM MissingReport r WHERE r.missing = :missing")
    List<MissingReport> findAllByMissing(@Param("missing") Missing missing);

    @Query("SELECT r FROM GeneralReport r WHERE r.regionCode = :regionCode")
    List<GeneralReport> findAllByRegionCodeAndMissingNull(long regionCode);
}
