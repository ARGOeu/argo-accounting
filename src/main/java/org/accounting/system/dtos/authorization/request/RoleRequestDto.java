package org.accounting.system.dtos.authorization.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.dtos.authorization.CollectionAccessPermissionDto;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Schema(name="RoleRequest", description="An object represents a request for creating a new Role.")
public class RoleRequestDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            required = true,
            description = "The role name.",
            example = "metric_definition_inspector"
    )
    @JsonProperty("name")
    @NotEmpty(message = "role name may not be empty.")
    public String name;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The role description explains the operations a user or a service is expected to perform.",
            example = "This role is responsible for creating ..."
    )
    @JsonProperty("description")
    public String description;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = CollectionAccessPermissionDto.class,
            required = true,
            minItems = 1,
            description = "This list encapsulates the access permissions upon the API collections. It should have at least one entry of CollectionPermission."
    )
    @JsonProperty("collections_access_permissions")
    @NotEmpty(message = "collections_access_permissions should have at least one entry.")
    public Set<@Valid CollectionAccessPermissionDto> collectionsAccessPermissions = new HashSet<>();
}