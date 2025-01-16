package org.accounting.system.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="Version", description="This object encapsulates the Accounting Service API version.")
public class VersionDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Accounting Service version.",
            example = "1.1.0"
    )
    @JsonProperty("version")
    public String version;
}
