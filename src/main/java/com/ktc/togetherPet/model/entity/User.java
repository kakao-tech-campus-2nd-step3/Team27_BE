package com.ktc.togetherPet.model.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(nullable = false)
    private String email;

    @Setter
    @Getter
    @Column(nullable = true)
    private String name;

    @Setter
    @ManyToOne(targetEntity = Region.class)
    @JoinColumn(name = "region_id", nullable = true)
    private Region region;

    @Getter
    @Setter
    @ManyToOne(targetEntity = Pet.class)
    @JoinColumn(name = "pet_id", nullable = true)
    private Pet pet;

    public User(String email) {
        this.email = email;
    }

}
