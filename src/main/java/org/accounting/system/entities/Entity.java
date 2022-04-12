package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import org.accounting.system.beans.RequestInformation;
import org.bson.types.ObjectId;

import javax.enterprise.inject.spi.CDI;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Entity {

    @EqualsAndHashCode.Include
    private ObjectId id;

    private String creatorId;

    public Entity(){
        RequestInformation requestInformation = CDI.current().select(RequestInformation.class).get();
        this.creatorId = requestInformation.getSubjectOfToken();
    }

    public String getCreatorId() {
        return creatorId;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}
