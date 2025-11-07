package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Capacity extends Entity {

    @BsonId
    private String id;

    @EqualsAndHashCode.Include
    @BsonProperty("installation_id")
    private String installationId;

    @EqualsAndHashCode.Include
    @BsonProperty("metric_definition_id")
    private String metricDefinitionId;

    @BsonProperty("registered_on")
    private LocalDateTime registeredOn;

    private BigDecimal value;
}
