package org.accounting.system.dtos.acl.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema(name="RoleAccessControlResponse", description="An object represents the stored Access Control Entry.")
public class RoleAccessControlResponseDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "who is the id of a client that the Access Control grants or denies access.",
            example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6"
    )
    @JsonProperty("who")
    public String who;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "This component is a set of roles.",
            example = "[\n" +
                    "       \"project_admin\"  \n" +
                    "   ]"
    )
    @JsonProperty("roles")
    public Set<String> roles;
}
