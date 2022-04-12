package org.accounting.system.dtos.acl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.constraints.StringEnumeration;
import org.accounting.system.enums.acl.AccessControlPermission;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Schema(name="AccessControlUpdate", description="An object represents a request for updating an Access Control Entry.")
public class AccessControlUpdateDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = AccessControlPermission.class,
            required = true,
            description = "The set of permissions to be updated.",
            minItems = 1
    )
    @JsonProperty("permissions")
    @NotEmpty(message = "permissions should have at least one entry.")
    public Set<@Valid @StringEnumeration(enumClass = AccessControlPermission.class, message = "entry") String> permissions;
}
