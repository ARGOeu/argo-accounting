package org.accounting.system.dtos.entitlement;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="EntitlementRequest", description="This object represents a request for creating a new Entitlement.")
public class EntitlementRequest {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Entitlement's name.",
            example = "urn:geant:sandbox.eosc-beyond.eu:core:integration:group:accounting:operations:resources:role=admin"
    )
    @JsonProperty("name")
    @NotEmpty(message = "name may not be empty.")
    public String name;
}
