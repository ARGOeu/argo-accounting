package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import org.accounting.system.beans.RequestInformation;

import javax.enterprise.inject.spi.CDI;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Entity {

    @EqualsAndHashCode.Include
    private String creatorId;

    public Entity(){
        RequestInformation requestInformation = CDI.current().select(RequestInformation.class).get();
        this.creatorId = requestInformation.getSubjectOfToken();
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}
