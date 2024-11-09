package com.ktc.togetherPet.model.entity.report;

import static lombok.AccessLevel.PROTECTED;

import com.ktc.togetherPet.model.entity.Region;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.Location;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("GENERAL")
@NoArgsConstructor(access = PROTECTED)
public class GeneralReport extends ReportBase {

    public GeneralReport(
        User user,
        LocalDateTime timestamp,
        Location location,
        Region region,
        String description
    ) {
        super(user, timestamp, location, region, description);
    }
}
