package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@JsonPropertyOrder({ "allocation_id", "valid_from", "valid_to" })
public class BasisAllocationDto {

    @Schema(type = SchemaType.STRING, description = "The id of the Credit Allocation this balance is based on.", example = "64f1a2b3c4d5e6f7a8b9c0d1")
    @JsonProperty("allocation_id")
    public String allocationId;

    @Schema(type = SchemaType.STRING, description = "The start of this allocation's period (inclusive). The balance window begins here.", example = "2026-08-01T00:00:00Z")
    @JsonProperty("valid_from")
    public Instant validFrom;

    @Schema(type = SchemaType.STRING, description = "The end of this allocation's period (exclusive).", example = "2026-09-01T00:00:00Z")
    @JsonProperty("valid_to")
    public Instant validTo;
}
