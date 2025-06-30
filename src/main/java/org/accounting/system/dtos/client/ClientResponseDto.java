package org.accounting.system.dtos.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="ClientResponse", description="An object represents the registered Client.")
public class ClientResponseDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "An identifier for the client which is globally unique.",
            example = "xyz@example.org"
    )
    @JsonProperty("id")
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
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Date and time when the user registered on the service.",
            example = "2024-09-13T09:38:47.116"
    )
    @JsonProperty("registered_on")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String registeredOn;

    @Schema(
            type = SchemaType.BOOLEAN,
            implementation = Boolean.class,
            description = "Whether a client is system admin or not.",
            example = "false"
    )
    @JsonProperty("system_admin")
    public boolean systemAdmin;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The issuer URI that issued the access token the client receives.",
            example = "http://localhost:58080/realms/quarkus"
    )
    @JsonProperty("issuer")
    public String issuer;
}