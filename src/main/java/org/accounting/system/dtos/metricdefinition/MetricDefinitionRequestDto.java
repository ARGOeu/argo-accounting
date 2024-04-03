package org.accounting.system.dtos.metricdefinition;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.accounting.system.constraints.MetricTypeNotFound;
import org.accounting.system.constraints.UnitTypeNotFound;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="MetricDefinitionRequest", description="An object represents a request for creating a Metric Definition.")
public class MetricDefinitionRequestDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The name of the Metric.",
            example = "weight",
            required = true
    )
    @JsonProperty("metric_name")
    @NotEmpty(message = "metric_name may not be empty.")
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
            description = "Metric Type of the Metric.",
            example = "aggregated",
            required = true
    )
    @JsonProperty("metric_type")
    @NotEmpty(message = "metric_type may not be empty.")
    @MetricTypeNotFound
    public String metricType;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unit Type of the Metric.",
            example = "kg",
            required = true
    )
    @JsonProperty("unit_type")
    @NotEmpty(message = "unit_type may not be empty.")
    @UnitTypeNotFound
    public String unitType;
}
