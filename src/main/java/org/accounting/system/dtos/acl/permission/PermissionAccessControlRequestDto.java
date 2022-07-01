package org.accounting.system.dtos.acl.permission;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.constraints.StringEnumeration;
import org.accounting.system.enums.acl.AccessControlPermission;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Schema(name="PermissionAccessControlRequest", description="An object represents a request for creating an Access Control Entry.")
public class PermissionAccessControlRequestDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = AccessControlPermission.class,
            required = true,
            description = "This component is a set of permissions.",
            minItems = 1
    )
    @JsonProperty("permissions")
    @NotEmpty(message = "permissions should have at least one entry.")
    public Set<@Valid @StringEnumeration(enumClass = AccessControlPermission.class, message = "entry") String> permissions;
}
