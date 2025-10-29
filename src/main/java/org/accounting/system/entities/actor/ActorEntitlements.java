package org.accounting.system.entities.actor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.accounting.system.entities.Entity;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ActorEntitlements extends Entity {

    @BsonId
    private String id;

    @EqualsAndHashCode.Include
    @BsonProperty("actor_id")
    private String actorId;

    @EqualsAndHashCode.Include
    @BsonProperty("entitlement_id")
    private String entitlementId;

    @BsonProperty("assigned_at")
    private LocalDateTime assignedAt;
}
