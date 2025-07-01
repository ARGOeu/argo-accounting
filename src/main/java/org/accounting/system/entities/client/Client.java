package org.accounting.system.entities.client;

import lombok.Getter;
import lombok.Setter;
import org.accounting.system.entities.Entity;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class Client extends Entity {

    @BsonId
    private String id;

    private String name;

    private String email;

    private boolean systemAdmin;

    private Set<String> roles;

    private LocalDateTime registeredOn;

    private String issuer;
}
