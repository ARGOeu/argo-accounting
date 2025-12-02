package org.accounting.system.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.accounting.system.constraints.CheckDateFormat;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name="CapacityRequest", description="An object represents a request for creating a capacity for an installation.")
public class CapacityRequest {

    @Schema(
            type = SchemaType.NUMBER,
            implementation = BigDecimal.class,
            description = "The capacity value.",
            required = true,
            example = "1000"
    )
    @JsonProperty("value")
    @NotNull(message = "value may not be empty.")
    public BigDecimal value;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The metric definition id.",
            required = true,
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("metric_definition_id")
    @NotEmpty(message = "metric_definition_id may not be empty.")
    @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:")
    public String metricDefinitionId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The date the capacity registered on.",
            example = "2024-09-13"
    )
    @JsonProperty("registered_on")
    @CheckDateFormat(pattern = "yyyy-MM-dd", message = "Valid date format is yyyy-MM-dd.")
    public String registeredOn;
}
