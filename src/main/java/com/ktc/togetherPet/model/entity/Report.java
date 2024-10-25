package com.ktc.togetherPet.model.entity;

import static lombok.AccessLevel.PROTECTED;

import com.ktc.togetherPet.model.vo.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report")
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
public class Report {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Getter
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timeStamp;

    @Getter
    @Embedded
    private Location location;

    @Column(name = "region_code", nullable = false)
    private long regionCode;

    @Getter
    @Column(name = "color")
    private String color;

    @Getter
    @Setter
    @ManyToOne(targetEntity = Breed.class)
    @JoinColumn(name = "breed_id")
    private Breed breed;

    @Setter
    @ManyToOne(targetEntity = Missing.class)
    @JoinColumn(name = "missing_id")
    private Missing missing;

    @Getter
    @Column(name = "description", nullable = false)
    private String description;

    @Getter
    @Setter
    @Column(name = "gender")
    private String gender;

    public Report(
        User user,
        LocalDateTime timestamp,
        Location location,
        long regionCode,
        String description
    ) {
        this.user = user;
        this.timeStamp = timestamp;
        this.location = location;
        this.regionCode = regionCode;
        this.description = description;
    }
}
