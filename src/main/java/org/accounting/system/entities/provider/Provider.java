package org.accounting.system.entities.provider;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.accounting.system.entities.Entity;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDateTime;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
public class Provider extends Entity {

    @BsonId
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String website;
    private String abbreviation;
    private String logo;
    @BsonProperty("external_id")
    private String externalId;
    @BsonProperty("registered_on")
    private LocalDateTime registeredOn;
}
