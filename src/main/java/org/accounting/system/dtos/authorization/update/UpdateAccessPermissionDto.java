package org.accounting.system.dtos.authorization.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.accounting.system.constraints.StringEnumeration;
import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="UpdateAccessPermission", description="By combining the attributes operation and access_type you can generate an access permission.")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UpdateAccessPermissionDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = Operation.class,
            description = "The available API operations.",
            example = "CREATE"
    )
    @JsonProperty("operation")
    @StringEnumeration(enumClass = Operation.class, message = "operation")
    @EqualsAndHashCode.Include
    public String operation;

    @Schema(
            type = SchemaType.STRING,
            implementation = AccessType.class,
            description = "The available API access types.",
            example = "ALWAYS"
    )
    @JsonProperty("access_type")
    @StringEnumeration(enumClass = AccessType.class, message = "access_type")
    @EqualsAndHashCode.Include
    public String accessType;
}