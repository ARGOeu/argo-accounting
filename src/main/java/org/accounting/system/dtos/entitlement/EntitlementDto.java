package org.accounting.system.dtos.entitlement;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="EntitlementDto", description="An object represents an entitlement.")
public class EntitlementDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The database entitlement id.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The entitlement's name.",
            example = "urn:geant:sandbox.eosc-beyond.eu:core:integration:group:accounting:operations:resources:role=admin"
    )
    @JsonProperty("name")
    public String name;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Date and time when the entitlement registered on the service.",
            example = "2024-09-13T09:38:47.116"
    )
    @JsonProperty("registered_on")
    public String registeredOn;
}
