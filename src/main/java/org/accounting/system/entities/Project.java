package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Project extends Entity{

    @BsonId
    @EqualsAndHashCode.Include
    private String id;
    @BsonProperty("acronym")
    private String acronym;

    @BsonProperty("title")
    private String title;

    @BsonProperty("start_date")
    private String startDate;

    @BsonProperty("end_date")
    private String endDate;

    @BsonProperty("call_identifier")
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
