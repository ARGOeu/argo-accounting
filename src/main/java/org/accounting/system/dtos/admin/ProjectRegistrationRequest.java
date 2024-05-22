package org.accounting.system.dtos.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema(name="ProjectRegistrationRequest", description="Payload object for registering projects. Contains a list of project IDs to be registered into the Accounting Service.")
public class ProjectRegistrationRequest {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            required = true,
            description = "Contains a list of project IDs to be registered into the Accounting Service.",
            minItems = 1,
            example = "[\n" +
                    "       888743,\n" +
                    "       894921,\n" +
                    "       101017567\n" +
                    "   ]"
    )
    @JsonProperty("projects")
    @NotEmpty(message = "projects should have at least one entry.")
    public Set<String> projects;
}
