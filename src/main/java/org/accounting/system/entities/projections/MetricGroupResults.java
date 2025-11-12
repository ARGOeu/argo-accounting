package org.accounting.system.entities.projections;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({ "metric_definition_id", "metric_name", "metric_description", "unit_type", "metric_type", "periods"})
@Schema(name="MetricGroupResults", description="Metrics per metric definitions.")
public class MetricGroupResults {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The unique id for the Metric Definition.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("metric_definition_id")
    private String metricDefinitionId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The name of the Metric.",
            example = "weight"
    )
    @JsonProperty("metric_name")
    private String metricName;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Description of how the metric is collected.",
            example = "The weight of a person"
    )
    @JsonProperty("metric_description")
    private String metricDescription;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unit Type of the Metric.",
            example = "kg"
    )
    @JsonProperty("unit_type")
    private String unitType;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Metric Type of the Metric.",
            example = "aggregated"
    )
    @JsonProperty("metric_type")
    private String metricType;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = CapacityPeriod.class,
            description = "The capacity periods."
    )
    @JsonProperty("periods")
    private List<CapacityPeriod> periods = new ArrayList<>();
}
