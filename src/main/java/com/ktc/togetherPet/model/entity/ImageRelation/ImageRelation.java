package com.ktc.togetherPet.model.entity.ImageRelation;


import com.ktc.togetherPet.model.entity.Image;
import com.ktc.togetherPet.model.entity.Report;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "imageRelation")
public class ImageRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_entity_type", nullable = false)
    private ImageEntityType imageEntityType;

    //todo: Report, Pet, Missing 연관 관계를 entity_id로 통합 관리하도록 해야함
    @ManyToOne
    @JoinColumn(name = "report_id", nullable = true)
    private Report report;

    /**todo: Report, Pet, Missing 연관 관계를 entity_id로 통합 관리하도록 해야함
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
     **/

    @ManyToOne(targetEntity = Image.class)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    public ImageRelation() {
    }

    public ImageRelation(ImageEntityType imageEntityType, Image image) {
        this.imageEntityType = imageEntityType;
        this.image = image;
    }
}
