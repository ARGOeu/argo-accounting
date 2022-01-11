package org.accounting.system.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

@Schema(name="MetricRegistrationRequest", description="An object represents a request for creating a Metric Registration.")
public class MetricRegistrationDtoRequest {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The name of the Virtual Access Metric.",
            example = "weight",
            required = true
    )
    @JsonProperty("metric_name")
    @NotEmpty(message = "Metric name may not be empty.")
    public String metricName;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Description of how the metric is collected.",
            example = "The weight of a person"
    )
    @JsonProperty("metric_description")
    public String metricDescription;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unit Type of the Virtual Access Metric.",
            example = "kg",
            required = true
    )
    @JsonProperty("unit_type")
    @NotEmpty(message = "Unit type may not be empty.")
    public String unitType;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Metric Type of the Virtual Access Metric.",
            example = "aggregated",
            required = true
    )
    @JsonProperty("metric_type")
    @NotEmpty(message = "Metric type may not be empty.")
    public String metricType;

}
