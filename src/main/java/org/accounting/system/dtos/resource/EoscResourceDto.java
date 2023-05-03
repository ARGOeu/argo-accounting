package org.accounting.system.dtos.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="EoscResource", description="An object represents an Eosc Resource.")
public class EoscResourceDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Resource unique identifier.",
            example = "openaire.european_marine_science_openaire_dashboard"
    )
    @JsonProperty("id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Resource name.",
            example = "European Marine Science OpenAIRE Community Gateway"
    )
    @JsonProperty("name")
    public String name;
}
