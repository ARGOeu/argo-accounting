package org.accounting.system.dtos.acl.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.enums.Collection;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema(name="RoleAccessControlResponse", description="An object represents the stored Access Control Entry.")
public class RoleAccessControlResponseDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unique ID of the stored Access Control Entry.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "who is the id of a client that the Access Control grants or denies access.",
            example = "fbdb4e4a-6e93-4b08-a1e7-0b7bd08520a6"
    )
    @JsonProperty("who")
    public String who;

    @Schema(
            type = SchemaType.STRING,
            implementation = Collection.class,
            description = "collection is a collection name that the entity belongs to.",
            example = "Project"
    )
    @JsonProperty("collection")
    public String collection;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "entity is the id of the entity to which the permissions apply.",
            example = "745690"
    )
    @JsonProperty("entity")
    public String entity;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = String.class,
            description = "This component is a set of roles."
    )
    @JsonProperty("roles")
    public Set<String> roles;
}
