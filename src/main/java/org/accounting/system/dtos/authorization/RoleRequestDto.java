package org.accounting.system.dtos.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

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
            implementation = CollectionPermissionDto.class,
            required = true,
            description = "This list encapsulates the permissions upon the API collections. It should have at least one entry of CollectionPermission.",
            minItems = 1
    )
    @JsonProperty("collection_permission_list")
    @NotEmpty(message = "collection_permission_list should have at least one entry.")
    public List<@Valid CollectionPermissionDto> collectionPermission;
}