package com.ktc.togetherPet.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "imageRelation")
public class ImageRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false)
    private Enum entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "image_id", nullable = false)
    private Long imageId;

    @Column(name = "pet_id", nullable = true)
    private Long petId;

    @Column(name = "missing_animal_id", nullable = true)
    private Long missingAnimalId;

    @Column(name = "report_id", nullable = true)
    private Long reportId;

    public ImageRelation() {
    }

    public ImageRelation(Enum entityType, Long entityId, Long imageId, Long petId,
        Long missingAnimalId, Long reportId) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.imageId = imageId;
        this.petId = petId;
        this.missingAnimalId = missingAnimalId;
        this.reportId = reportId;
    }
}
