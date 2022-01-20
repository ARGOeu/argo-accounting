package org.accounting.system.dtos;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="Response", description="Illustrates if an API operation is successful or not.")
public class InformativeResponse {

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Integer.class,
            description = "A code that indicates whether a specific request has been completed.",
            example = "The http status"
    )
    public int code;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "A message that informs whether a specific request has been completed.",
            example = "An informative message relative to the process"
    )
    public String message;
}
