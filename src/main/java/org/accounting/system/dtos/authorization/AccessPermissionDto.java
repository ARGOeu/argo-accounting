package org.accounting.system.dtos.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import org.accounting.system.constraints.StringEnumeration;
import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="AccessPermission", description="By combining the attributes operation and access_type you can generate an access permission.")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccessPermissionDto {

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
    @EqualsAndHashCode.Include
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
    @EqualsAndHashCode.Include
    public String accessType;
}
