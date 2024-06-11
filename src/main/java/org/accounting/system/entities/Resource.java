package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
public class Resource extends Entity {

    @BsonId
    @EqualsAndHashCode.Include
    private String id;

    @BsonProperty("name")
    private String name;
}
