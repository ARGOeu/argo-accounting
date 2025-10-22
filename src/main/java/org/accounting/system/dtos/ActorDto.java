package org.accounting.system.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="ActorDto", description="An object represents the registered Actor.")
public class ActorDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The database actor id.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "An oidc identifier for the actor.",
            example = "xyz@example.org"
    )
    @JsonProperty("oidc_id")
    public String oidcId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The actor's full name.",
            example = "John Doe"
    )
    @JsonProperty("name")
    public String name;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The actor's email.",
            example = "john.doe@example.org"
    )
    @JsonProperty("email")
    public String email;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Date and time when the actor registered on the service.",
            example = "2024-09-13T09:38:47.116"
    )
    @JsonProperty("registered_on")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String registeredOn;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The issuer URI that issued the access token the actor receives.",
            example = "http://localhost:58080/realms/quarkus"
    )
    @JsonProperty("issuer")
    public String issuer;
}
