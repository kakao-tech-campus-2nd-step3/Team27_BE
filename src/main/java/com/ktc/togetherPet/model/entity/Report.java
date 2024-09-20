package com.ktc.togetherPet.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "report_timestamp", nullable = false)
    private String reportTimeStamp;

    @Column(name = "report_latitude", nullable = false)
    private Float reportLatitude;

    @Column(name = "report_longitude", nullable = false)
    private Float reportLongitude;

    @Column(name = "missing_animal_id", nullable = true)
    private Long missingAnimalId;

    public Report() {}

    public Report(Long userId, String reportTimeStamp, Float reportLatitude, Float reportLongitude, Long missingAnimalId) {
        this.userId = userId;
        this.reportTimeStamp = reportTimeStamp;
        this.reportLatitude = reportLatitude;
        this.reportLongitude = reportLongitude;
        this.missingAnimalId = missingAnimalId;
    }
}
