package org.accounting.system.dtos.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.constraints.StringEnumeration;
import org.accounting.system.entities.authorization.Permission;
import org.accounting.system.enums.Collection;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Schema(name="CollectionPermission", description="An object represents the permissions upon a collection.")
public class CollectionPermissionDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = Permission.class,
            required = true,
            description = "A list of permissions. It should have at least one entry.",
            minItems = 1
    )
    @JsonProperty("permissions")
    @NotEmpty(message = "permissions list should have at least one entry.")
    public List<@Valid PermissionDto> permissions;

    @Schema(
            type = SchemaType.STRING,
            implementation = Collection.class,
            required = true,
            description = "The name of the collection to which the permissions apply.",
            example = "MetricDefinition"
    )
    @JsonProperty("collection")
    @NotEmpty(message = "collection may not be empty.")
    @StringEnumeration(enumClass = Collection.class, message = "collection")
    public String collection;
}
