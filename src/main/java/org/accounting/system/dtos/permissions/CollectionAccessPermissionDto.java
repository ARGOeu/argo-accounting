package org.accounting.system.dtos.permissions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CollectionAccessPermissionDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = AccessPermissionDto.class,
            description = "A list of access permissions. It should have at least one entry.",
            minItems = 1
    )
    @JsonProperty("access_permissions")
    @EqualsAndHashCode.Include
    public Set<AccessPermissionDto> accessPermissions;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The name of the collection to which the access permissions apply.",
            example = "MetricDefinition"
    )
    @JsonProperty("collection")
    @EqualsAndHashCode.Include
    public String collection;
}
