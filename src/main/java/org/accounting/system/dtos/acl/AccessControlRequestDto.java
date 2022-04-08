package org.accounting.system.dtos.acl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.enums.acl.AccessControlPermission;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Schema(name="AccessControlRequest", description="An object represents a request for creating an Access Control Entry.")
public class AccessControlRequestDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "who is the id of a Service/User that the Access Control grants access.",
            example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6",
            required = true
    )
    @JsonProperty("who")
    @NotEmpty(message = "who may not be empty.")
    public String who;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = AccessControlPermission.class,
            required = true,
            description = "This component is a set of permissions.",
            minItems = 1
    )
    @JsonProperty("permissions")
    @NotEmpty(message = "permissions should have at least one entry.")
    public Set<AccessControlPermission> permissions;
}
