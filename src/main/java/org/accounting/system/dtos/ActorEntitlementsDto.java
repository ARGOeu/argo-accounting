package org.accounting.system.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="ActorEntitlementsDto", description="An object represents the actor's entitlement.")
public class ActorEntitlementsDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The database id.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The actor id.",
            example = "607f1f77bcf86cd799439011"
    )
    @JsonProperty("actor_id")
    public String actorId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The entitlement id.",
            example = "707f1f77bcf86cd799439011"
    )
    @JsonProperty("entitlement_id")
    public String entitlementId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Date and time when the entitlement assigned on the actor.",
            example = "2024-09-13T09:38:47.116"
    )
    @JsonProperty("assigned_at")
    public String assignedAt;
}
