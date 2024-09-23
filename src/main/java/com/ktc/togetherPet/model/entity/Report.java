package com.ktc.togetherPet.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timeStamp;

    @Column(name = "latitude", nullable = false)
    private Float latitude;

    @Column(name = "longitude", nullable = false)
    private Float longitude;

    @ManyToOne(targetEntity = Missing.class)
    @JoinColumn(name = "mssing_id", nullable = true)
    private Missing missing;

    public Report() {
    }

    public Report(User user, LocalDateTime timestamp, Float latitude, Float longitude,
        Missing missing) {
        this.user = user;
        this.timeStamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.missing = missing;
    }
}
