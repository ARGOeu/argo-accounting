package org.accounting.system.dtos.authorization.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema(name="AssignRoleRequest", description="This object should contain a list of Roles that are going to be assigned to a registered client.")
public class AssignRoleRequestDto {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            required = true,
            description = "This list contains the name of roles.",
            minItems = 1,
            example = "{\n" +
                    "   \"roles\":[\n" +
                    "      \"role_admin\",\n" +
                    "      \"metric_definition_creator\",\n" +
                    "      \"metric_inspector\"\n" +
                    "   ]\n" +
                    "}\n"
    )
    @JsonProperty("roles")
    @NotEmpty(message = "List should have at least one entry.")
    public Set<String> roles;
}