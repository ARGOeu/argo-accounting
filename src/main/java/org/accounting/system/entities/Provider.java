package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import org.bson.codecs.pojo.annotations.BsonId;

public class Provider extends Entity {

    @BsonId
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String website;
    private String abbreviation;
    private String logo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
