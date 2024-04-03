package org.accounting.system.dtos.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import org.accounting.system.constraints.StringEnumeration;
import org.accounting.system.enums.Collection;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;


@Schema(name="CollectionAccessPermission", description="An object represents the access permissions upon a collection.")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CollectionAccessPermissionDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = AccessPermissionDto.class,
            required = true,
            description = "A list of access permissions. It should have at least one entry.",
            minItems = 1
    )
    @JsonProperty("access_permissions")
    @NotEmpty(message = "access_permissions list should have at least one entry.")
    @EqualsAndHashCode.Include
    public Set<@Valid AccessPermissionDto> accessPermissions;

    @Schema(
            type = SchemaType.STRING,
            implementation = Collection.class,
            required = true,
            description = "The name of the collection to which the access permissions apply.",
            example = "MetricDefinition"
    )
    @JsonProperty("collection")
    @NotEmpty(message = "collection may not be empty.")
    @StringEnumeration(enumClass = Collection.class, message = "collection")
    @EqualsAndHashCode.Include
    public String collection;
}
