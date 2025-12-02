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
public class Entitlement extends Entity {

    @BsonId
    private String id;

    @EqualsAndHashCode.Include
    private String name;

    @BsonProperty("registered_on")
    private LocalDateTime registeredOn;
}
