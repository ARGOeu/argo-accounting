package org.accounting.system.dtos.metrictype;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="MetricType", description="An object represents a Metric Type.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricTypeDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The unique identifier of Metric Type.",
            readOnly = true,
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Metric Type.",
            example = "aggregated"
    )
    @JsonProperty("metric_type")
    @NotEmpty(message = "metric_type may not be empty.")
    public String metricType;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "A short description of Metric Type",
            example = "The sum of all values captured over the aggregation interval."
    )
    @NotEmpty(message = "description may not be empty.")
    @JsonProperty("description")
    public String description;

    @JsonProperty("creator_id")
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            readOnly = true,
            description = "The client's voperson_id who has created the Metric Type",
            example = "ee4r4fffff368faa27442e7@grnet.account"
    )
    public String creatorId;
}
