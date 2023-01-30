package org.accounting.system.clients.responses.openaire;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Entity {

    @JsonProperty("oaf:project")
    public Project project;
}
