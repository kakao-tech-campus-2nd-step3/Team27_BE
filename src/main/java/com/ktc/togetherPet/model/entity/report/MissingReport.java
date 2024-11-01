package com.ktc.togetherPet.model.entity.report;

import static lombok.AccessLevel.PROTECTED;

import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.Location;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("MISSING")
@NoArgsConstructor(access = PROTECTED)
public class MissingReport extends ReportBase {

    @ManyToOne(targetEntity = Missing.class)
    @JoinColumn(name = "missing_id")
    private Missing missing;

    public MissingReport(
        User user,
        LocalDateTime timestamp,
        Location location,
        long regionCode,
        String description,
        Missing missing
    ) {
        super(user, timestamp, location, regionCode, description);
        this.missing = missing;
    }
}
