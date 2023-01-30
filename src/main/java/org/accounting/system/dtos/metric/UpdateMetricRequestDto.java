package org.accounting.system.dtos.metric;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.constraints.ZuluTime;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Schema(name="UpdateMetricRequest", description="An object represents a request for updating a Metric.")
public class UpdateMetricRequestDto {
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Metric Definition to be updated.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("metric_definition_id")
    @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:")
    public String metricDefinitionId;

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the starting date time (Zulu timestamp) to be updated.",
            example = "2022-01-05T09:13:07Z"
    )
    @JsonProperty("time_period_start")
    @ZuluTime(message = "time_period_start", acceptEmptyValue = true)
    public String start;

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the end date time (Zulu timestamp) to be updated.",
            example = "2022-01-05T09:13:07Z"
    )
    @JsonProperty("time_period_end")
    @ZuluTime(message = "time_period_end", acceptEmptyValue = true)
    public String end;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Double.class,
            description = "Value of the metric for the given period to be updated.",
            example = "10.2"
    )
    @JsonProperty("value")
    public Double value;
}
