package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import org.bson.codecs.pojo.annotations.BsonId;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Project extends Entity{

    @BsonId
    @EqualsAndHashCode.Include
    private String id;
    private String acronym;
    private String title;
    private String startDate;
    private String endDate;
    private String callIdentifier;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCallIdentifier() {
        return callIdentifier;
    }

    public void setCallIdentifier(String callIdentifier) {
        this.callIdentifier = callIdentifier;
    }
}
