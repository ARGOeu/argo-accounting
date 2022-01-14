package org.accounting.system.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="UpdateMetricDefinitionRequest", description="An object represents a request for updating a Metric Definition.")
public class UpdateMetricDefinitionDtoRequest {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The name of the Metric to be updated.",
            example = "weight"
    )
    @JsonProperty("metric_name")
    public String metricName;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The description of the Metric to be updated.",
            example = "The weight of a person"
    )
    @JsonProperty("metric_description")
    public String metricDescription;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unit Type of the Metric to be updated.",
            example = "kg"
    )
    @JsonProperty("unit_type")
    public String unitType;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Metric Type of the Metric to be updated.",
            example = "aggregated"
    )
    @JsonProperty("metric_type")
    public String metricType;

}
