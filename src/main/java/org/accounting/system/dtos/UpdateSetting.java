package org.accounting.system.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "UpdateSetting", description = "Represents an object for updating a setting value.")
public class UpdateSetting {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The value to be updated.",
            example = "database",
            required = true
    )
    @JsonProperty("value")
    @NotEmpty(message = "value may not be empty.")
    public String value;
}
