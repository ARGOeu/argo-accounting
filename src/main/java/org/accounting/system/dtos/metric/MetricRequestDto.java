package org.accounting.system.dtos.metric;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.constraints.ValidateStartEndTimestamp;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Schema(name="MetricRequest", description="An object represents a request for creating a Metric.")
@ValidateStartEndTimestamp(message = "Validate start/end timestamp")
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
    @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:")
    public String metricDefinitionId;

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the starting date time (Zulu timestamp).",
            example = "2022-01-05T09:13:07Z",
            required = true
    )
    @JsonProperty("time_period_start")
    public String start;

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the end date time (Zulu timestamp).",
            example = "2022-01-05T09:13:07Z",
            required = true
    )
    @JsonProperty("time_period_end")
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

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Group ID associated with a metric.",
            example = "group-id"
    )
    @JsonProperty("group_id")
    public String groupId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "User ID associated with a metric.",
            example = "user-id"
    )
    @JsonProperty("user_id")
    public String userId;
}