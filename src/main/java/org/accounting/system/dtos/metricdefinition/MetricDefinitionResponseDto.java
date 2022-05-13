package org.accounting.system.dtos.metricdefinition;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="MetricDefinitionResponse", description="An object represents the stored Metric Definition.")
public class MetricDefinitionResponseDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The unique id for the Metric Definition.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("metric_definition_id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The name of the Metric.",
            example = "weight"
    )
    @JsonProperty("metric_name")
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
            description = "Unit Type of the Metric.",
            example = "kg"
    )
    @JsonProperty("unit_type")
    public String unitType;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Metric Type of the Metric.",
            example = "aggregated"
    )
    @JsonProperty("metric_type")
    public String metricType;


}
