package org.accounting.system.entities.projections;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Schema(name="CapacityPeriod", description="The capacity period.")
public class CapacityPeriod {

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the starting date time (Zulu timestamp).",
            example = "2022-01-05T09:13:07Z"
    )
    @JsonProperty("from")
    private Instant from;

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the ending date time (Zulu timestamp).",
            example = "2022-01-05T09:13:07Z"
    )
    @JsonProperty("to")
    private Instant to;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Double.class,
            description = "Sum of metric values for the specified metric definition during the given time period.",
            example = "1234.56"
    )
    @JsonProperty("total_value")
    private double totalValue;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = BigDecimal.class,
            description = "Capacity value.",
            example = "1000"
    )
    @JsonProperty("capacity_value")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal capacityValue;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = BigDecimal.class,
            description = "Usage percentage.",
            example = "75"
    )
    @JsonProperty("usage_percentage")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal usagePercentage;
}
