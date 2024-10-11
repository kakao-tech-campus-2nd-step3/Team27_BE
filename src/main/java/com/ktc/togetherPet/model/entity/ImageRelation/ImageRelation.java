package com.ktc.togetherPet.model.entity.ImageRelation;


import static lombok.AccessLevel.PROTECTED;

import com.ktc.togetherPet.model.entity.Image;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "imageRelation")
@NoArgsConstructor(access = PROTECTED)
public class ImageRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_entity_type", nullable = false)
    private ImageEntityType imageEntityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Getter
    @ManyToOne(targetEntity = Image.class)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    public ImageRelation(
        ImageEntityType imageEntityType,
        Long entityId,
        Image image
    ) {
        this.imageEntityType = imageEntityType;
        this.entityId = entityId;
        this.image = image;
    }
}
