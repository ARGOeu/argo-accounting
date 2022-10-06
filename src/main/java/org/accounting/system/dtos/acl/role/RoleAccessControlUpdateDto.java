package org.accounting.system.dtos.acl.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Schema(name="RoleAccessControlUpdate", description="An object represents a request for updating an Access Control Entry.")
public class RoleAccessControlUpdateDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            required = true,
            description = "The set of roles to be updated.",
            minItems = 1,
            example = "[\n" +
                    "       \"project_admin\"\n" +
                    "   ]"
    )
    @JsonProperty("roles")
    @NotEmpty(message = "roles should have at least one entry.")
    public Set<String> roles;
}
