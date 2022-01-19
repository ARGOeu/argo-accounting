package org.accounting.system.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Schema(name="MetricResponse", description="An object represents the stored Metric.")
public class MetricResponseDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unique ID of the metric."
    )
    @JsonProperty("metric_id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unique Identifier from the resource."
    )
    @JsonProperty("resource_id")
    public String resourceId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Reference Id from the metric definition."
    )
    @JsonProperty("metric_definition_id")
    public String metricDefinitionId;

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the starting date time (Zulu timestamp).",
            example = "2022-01-05T09:13:07Z"
    )
    @JsonProperty("time_period_start")
    public Instant start;

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the end date time (Zulu timestamp).",
            example = "2022-01-05T09:13:07Z"
    )
    @JsonProperty("time_period_end")
    public Instant end;

    @Schema(
            type = SchemaType.STRING,
            implementation = Double.class,
            description = "Value of the metric for the given period."
    )
    @JsonProperty("value")
    public double value;

}
