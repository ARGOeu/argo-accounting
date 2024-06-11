package org.accounting.system.dtos.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="ResourceRequest", description="This object represents a request for creating a new Resource.")
public class ResourceRequest {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Resource unique identifier.",
            example = "openaire.european_marine_science_openaire_dashboard"
    )
    @JsonProperty("id")
    @NotEmpty(message = "id may not be empty.")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Resource name.",
            example = "European Marine Science OpenAIRE Community Gateway"
    )
    @JsonProperty("name")
    @NotEmpty(message = "name may not be empty.")
    public String name;
}
