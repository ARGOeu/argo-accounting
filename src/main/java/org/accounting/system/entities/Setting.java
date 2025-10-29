package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.accounting.system.enums.APISetting;
import org.bson.codecs.pojo.annotations.BsonId;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Setting extends Entity {

    @BsonId
    private String id;
    @EqualsAndHashCode.Include
    private APISetting key;
    private String value;
}
