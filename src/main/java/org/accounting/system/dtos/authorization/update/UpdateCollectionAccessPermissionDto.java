package org.accounting.system.dtos.authorization.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.accounting.system.constraints.StringEnumeration;
import org.accounting.system.enums.Collection;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.Valid;
import java.util.Set;

@Schema(name="UpdateCollectionAccessPermission", description="An object represents the access permissions upon a collection.")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UpdateCollectionAccessPermissionDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = UpdateAccessPermissionDto.class,
            description = "A list of access permissions."
    )
    @JsonProperty("access_permissions")
    @EqualsAndHashCode.Include
    public Set<@Valid UpdateAccessPermissionDto> accessPermissions;

    @Schema(
            type = SchemaType.STRING,
            implementation = Collection.class,
            description = "The name of the collection to which the access permissions apply.",
            example = "MetricDefinition"
    )
    @JsonProperty("collection")
    @StringEnumeration(enumClass = Collection.class, message = "collection")
    @EqualsAndHashCode.Include
    public String collection;
}