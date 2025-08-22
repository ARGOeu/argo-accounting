package org.accounting.system.entities.installation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.accounting.system.entities.Entity;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
public class Installation extends Entity {

    @BsonId
    private String id;

    @EqualsAndHashCode.Include
    @BsonProperty("project")
    private String project;

    @EqualsAndHashCode.Include
    @BsonProperty("organisation")
    private String organisation;

    @BsonProperty("infrastructure")
    private String infrastructure;

    @EqualsAndHashCode.Include
    @BsonProperty("installation")
    private String installation;

    @BsonProperty("resource")
    private String resource;

    @BsonProperty("unit_of_access")
    private ObjectId unitOfAccess;

    @BsonProperty("external_id")
    private String externalId;
}