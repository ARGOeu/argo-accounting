package org.accounting.system.dtos.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.constraints.StringEnumeration;
import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

@Schema(name="Permission", description="By combining the attributes operation and access_type you can generate a permission.")
public class PermissionDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = Operation.class,
            required = true,
            description = "The available API operations.",
            example = "CREATE"
    )
    @JsonProperty("operation")
    @NotEmpty(message = "operation may not be empty.")
    @StringEnumeration(enumClass = Operation.class, message = "operation")
    public String operation;

    @Schema(
            type = SchemaType.STRING,
            implementation = AccessType.class,
            required = true,
            defaultValue = "NEVER",
            description = "The available API access types.",
            example = "ALWAYS"
    )
    @JsonProperty("access_type")
    @NotEmpty(message = "access_type may not be empty.")
    @StringEnumeration(enumClass = AccessType.class, message = "access_type")
    public String accessType;
}
