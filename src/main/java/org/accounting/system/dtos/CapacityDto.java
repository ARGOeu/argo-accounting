package org.accounting.system.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name="CapacityDto", description="An object represents the installation capacities.")
public class CapacityDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The database id.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The installation id.",
            example = "607f1f77bcf86cd799439011"
    )
    @JsonProperty("installation_id")
    public String installationId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The metric definition id.",
            example = "707f1f77bcf86cd799439011"
    )
    @JsonProperty("metric_definition_id")
    public String metricDefinitionId;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = BigDecimal.class,
            description = "The capacity value.",
            example = "1000"
    )
    @JsonProperty("value")
    public BigDecimal value;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Date and time when the capacity registered on the installation.",
            example = "2024-09-13T09:38:47.116"
    )
    @JsonProperty("registered_on")
    public String registeredOn;
}
