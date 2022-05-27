package org.accounting.system.entities;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class Installation extends Entity{

    private ObjectId id;

    @BsonProperty("organisation")
    private String organisation;

    @BsonProperty("infrastructure")
    private String infrastructure;

    @BsonProperty("installation")
    private String installation;

    @BsonProperty("unit_of_access")
    private ObjectId unitOfAccess;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(String infrastructure) {
        this.infrastructure = infrastructure;
    }

    public String getInstallation() {
        return installation;
    }

    public void setInstallation(String installation) {
        this.installation = installation;
    }

    public ObjectId getUnitOfAccess() {
        return unitOfAccess;
    }

    public void setUnitOfAccess(ObjectId unitOfAccess) {
        this.unitOfAccess = unitOfAccess;
    }
}
