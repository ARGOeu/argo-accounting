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
public class Actor extends Entity {

    @BsonId
    private String id;

    private String name;

    private String email;

    @BsonProperty("registered_on")
    private LocalDateTime registeredOn;

    private String issuer;

    @EqualsAndHashCode.Include
    @BsonProperty("oidc_id")
    private String oidcId;
}
