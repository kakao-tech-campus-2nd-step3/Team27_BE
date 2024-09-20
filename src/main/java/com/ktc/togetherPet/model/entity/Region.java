package com.ktc.togetherPet.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "region")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long regionId;

    @Column(name = "region_name", nullable = false)
    private String regionName;

    @Column(name = "region_latitude", nullable = false)
    private Float regionLatitude;

    @Column(name = "region_longitude", nullable = false)
    private Float regionLongitude;

    public Region() {}

    public Region(String regionName, Float regionLatitude, Float regionLongitude) {
        this.regionName = regionName;
        this.regionLatitude = regionLatitude;
        this.regionLongitude = regionLongitude;
    }
}
