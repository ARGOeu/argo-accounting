package org.accounting.system.dtos.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema(name="ClientResponse", description="An object represents the registered Client.")
public class ClientResponseDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "An identifier for the client which is globally unique.",
            example = "xyz@example.org"
    )
    @JsonProperty("voperson_id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The client's full name.",
            example = "John Doe"
    )
    @JsonProperty("name")
    public String name;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The client's email.",
            example = "john.doe@example.org"
    )
    @JsonProperty("email")
    public String email;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "This list contains the name of roles.",
            example = "{\n" +
                    "   \"roles\":[\n" +
                    "      \"role_admin\",\n" +
                    "      \"metric_definition_creator\",\n" +
                    "      \"metric_inspector\"\n" +
                    "   ]\n" +
                    "}\n"
    )
    @JsonProperty("roles")
    public Set<String> roles;
}