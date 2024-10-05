package com.ktc.togetherPet.model.entity;

import com.ktc.togetherPet.model.vo.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "region")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    private Location location;

    public Region() {
    }

    public Region(String name, Location location) {
        this.name = name;
        this.location = location;
    }
}
