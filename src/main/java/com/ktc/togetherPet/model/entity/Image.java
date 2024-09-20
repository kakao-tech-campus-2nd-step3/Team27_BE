package com.ktc.togetherPet.model.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    public Image() {}

    public Image(String imagePath) {
        this.imagePath = imagePath;
    }
}
