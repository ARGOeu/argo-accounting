package org.accounting.system.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.constraints.ZuluTime;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;

@Schema(name="MetricRequest", description="An object represents a request for creating a Metric.")
public class MetricRequestDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Metric Definition to be assigned.",
            required = true,
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("metric_definition_id")
    @NotEmpty(message = "metric_definition_id may not be empty.")
    public String metricDefinitionId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unique Identifier from the resource.",
            required = true,
            example = "resource-id"
    )
    @JsonProperty("resource_id")
    @NotEmpty(message = "resource_id may not be empty.")
    public String resourceId;

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the starting date time (Zulu timestamp).",
            example = "2022-01-05T09:13:07Z",
            required = true
    )
    @JsonProperty("time_period_start")
    @ZuluTime(message = "time_period_start")
    @NotEmpty(message = "time_period_start may not be empty.")
    public String start;

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the end date time (Zulu timestamp).",
            example = "2022-01-05T09:13:07Z",
            required = true
    )
    @JsonProperty("time_period_end")
    @ZuluTime(message = "time_period_end")
    @NotEmpty(message = "time_period_end may not be empty.")
    public String end;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Double.class,
            description = "Value of the metric for the given period.",
            required = true,
            example = "10.2"
    )
    @JsonProperty("value")
    public double value;
}