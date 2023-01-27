package org.accounting.system.dtos.metrictype;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="UpdateMetricTypeRequest", description="An object represents a request for updating a Metric Type.")
public class UpdateMetricTypeRequestDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Unit Type.",
            example = "TB/year"
    )
    @JsonProperty("metric_type")
    public String metricType;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "A short description of Metric Type",
            example = "The sum of all values captured over the aggregation interval"
    )
    @JsonProperty("description")
    public String description;
}
