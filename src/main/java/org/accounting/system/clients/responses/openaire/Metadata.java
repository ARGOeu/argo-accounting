package org.accounting.system.clients.responses.openaire;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Metadata {

    @JsonProperty("oaf:entity")
    public Entity entity;
}
