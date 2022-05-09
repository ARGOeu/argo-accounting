package org.accounting.system.clients.responses.openaire;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Project {

    public Code code;
    public Acronym acronym;
    public Title title;

    @JsonProperty("startdate")
    public StartDate startDate;

    @JsonProperty("enddate")
    public EndDate endDate;

    @JsonProperty("callidentifier")
    public CallIdentifier callIdentifier;

}
