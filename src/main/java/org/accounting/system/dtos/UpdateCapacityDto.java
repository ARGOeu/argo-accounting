package org.accounting.system.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.constraints.CheckDateFormat;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name="UpdateCapacityDto", description="An object represents a request for updating an existing Capacity.")
public class UpdateCapacityDto {

    @Schema(
            type = SchemaType.NUMBER,
            implementation = BigDecimal.class,
            description = "The capacity value.",
            example = "1000"
    )
    @JsonProperty("value")
    public BigDecimal value;

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
