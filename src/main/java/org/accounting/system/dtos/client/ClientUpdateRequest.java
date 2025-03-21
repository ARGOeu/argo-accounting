package org.accounting.system.dtos.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="ClientUpdateRequest", description="An object for updating an existing Client.")
public class ClientUpdateRequest {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The client's full name.",
            example = "John Doe"
    )
    @JsonProperty("name")
    public String name;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The client's email.",
            example = "john.doe@example.org"
    )
    @JsonProperty("email")
    public String email;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Date and time when the user registered on the service. Must be yyyy-MM-dd'T'HH:mm:ss",
            example = "2024-09-13T09:38:47"
    )
    @JsonProperty("registered_on")
    public String registeredOn;
}
