package org.accounting.system.dtos.permissions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccessPermissionDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The available API operations.",
            example = "CREATE"
    )
    @JsonProperty("operation")
    @EqualsAndHashCode.Include
    public String operation;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            defaultValue = "NEVER",
            description = "The available API access types.",
            example = "ALWAYS"
    )
    @JsonProperty("access_type")
    @EqualsAndHashCode.Include
    public String accessType;
}
