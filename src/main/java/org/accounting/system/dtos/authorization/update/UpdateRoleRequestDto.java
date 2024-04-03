package org.accounting.system.dtos.authorization.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema(name="UpdateRoleRequest", description="An object represents a request for updating a Role.")
public class UpdateRoleRequestDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The role name.",
            example = "metric_definition_inspector"
    )
    @JsonProperty("name")
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
            implementation = UpdateCollectionAccessPermissionDto.class,
            description = "This list encapsulates the permissions upon the API collections."
    )
    @JsonProperty("collections_access_permissions")
    public Set<@Valid UpdateCollectionAccessPermissionDto> collectionsAccessPermissions;
}