package org.accounting.system.dtos.acl.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema(name="RoleAccessControlRequest", description="An object represents a request for creating an Access Control Entry.")
public class RoleAccessControlRequestDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            required = true,
            description = "This component is a set of roles.",
            minItems = 1,
            example = "[\n" +
                    "       \"project_admin\"\n" +
                    "   ]"
    )
    @JsonProperty("roles")
    @NotEmpty(message = "roles should have at least one entry.")
    public Set<String> roles;
}
