package com.ktc.togetherPet.model.entity;

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

@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timeStamp;

    @Embedded
    private Location location;

    @Column(name = "color", nullable = true)
    private String color;

    @ManyToOne(targetEntity = Breed.class)
    @JoinColumn(name = "breed_id", nullable = true)
    private Breed breed;

    @ManyToOne(targetEntity = Missing.class)
    @JoinColumn(name = "missing_id", nullable = true)
    private Missing missing;

    public Long getId() {
        return id;
    }

    public void setBreed(Breed breed) {
        this.breed = breed;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setMissing(Missing missing) {
        this.missing = missing;
    }

    public Report() {
    }

    public Report(User user, LocalDateTime timestamp, Location location) {
        this.user = user;
        this.timeStamp = timestamp;
        this.location = location;
    }
}
