package org.accounting.system.dtos.unittype;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="UpdateUnitTypeRequest", description="An object represents a request for updating a Unit Type.")
public class UpdateUnitTypeRequestDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Unit Type.",
            example = "TB/year"
    )
    @JsonProperty("unit_type")
    public String unit;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "A short description of Unit Type",
            example = "terabyte per year"
    )
    @JsonProperty("description")
    public String description;
}
