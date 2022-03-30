package org.accounting.system.dtos.authorization.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.constraints.StringEnumeration;
import org.accounting.system.enums.Collection;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.Valid;
import java.util.List;

@Schema(name="UpdateCollectionPermission", description="An object represents the permissions upon a collection.")
public class UpdateCollectionPermissionDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = UpdatePermissionDto.class,
            description = "A list of permissions."
    )
    @JsonProperty("permissions")
    public List<@Valid UpdatePermissionDto> permissions;

    @Schema(
            type = SchemaType.STRING,
            implementation = Collection.class,
            description = "The name of the collection to which the permissions apply.",
            example = "MetricDefinition"
    )
    @JsonProperty("collection")
    @StringEnumeration(enumClass = Collection.class, message = "collection")
    public String collection;
}