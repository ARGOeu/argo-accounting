package org.accounting.system.dtos.unittype;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="UnitType", description="An object represents a Unit Type.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitTypeDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The unique identifier of Unit Type.",
            readOnly = true,
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Unit Type.",
            example = "TB/year"
    )
    @JsonProperty("unit_type")
    @NotEmpty(message = "unit_type may not be empty.")
    public String unit;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "A short description of Unit Type",
            example = "terabyte per year"
    )
    @JsonProperty("description")
    @NotEmpty(message = "description may not be empty.")
    public String description;

    @JsonProperty("creator_id")
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            readOnly = true,
            description = "The client's id who has created the Unit Type.",
            example = "ee4r4fffff368faa27442e7@grnet.account"
    )
    public String creatorId;
}
