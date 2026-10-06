package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@JsonPropertyOrder({ "project_id", "group_id", "at", "is_snapshot", "warning", "allocation_in_effect",
        "basis_allocation", "allocated_credits", "consumed_credits", "balance", "reason", "installations" })
public class CreditBalanceResponseDto {

    @Schema(type = SchemaType.STRING, description = "The project id.", example = "707f1f77bcf86cd799439011")
    @JsonProperty("project_id")
    public String projectId;

    @Schema(type = SchemaType.STRING, description = "The group id.", example = "group-42")
    @JsonProperty("group_id")
    public String groupId;

    @Schema(type = SchemaType.STRING, description = "The resolved instant this balance was computed up to (exclusive): the start of the day after the requested 'at' date.", example = "2026-08-21T00:00:00Z")
    public Instant at;

    @Schema(type = SchemaType.BOOLEAN, description = "True when 'at' was explicitly provided in the request rather than defaulting to today.", example = "true")
    @JsonProperty("is_snapshot")
    public boolean isSnapshot;

    @Schema(type = SchemaType.STRING, description = "Present only when is_snapshot is true: this balance is a historical snapshot as of 'at' and says nothing about the current (true) balance.", nullable = true)
    public String warning;

    @Schema(type = SchemaType.BOOLEAN, description = "True if a Credit Allocation was in effect at 'at' (basis_allocation exists and 'at' is before its valid_to). False if no allocation has started yet, or the most recent one has already ended. When false, allocated_credits is 0 and only consumption after the basis allocation's valid_to (or all recorded consumption, if there is no basis allocation) is counted.", example = "true")
    @JsonProperty("allocation_in_effect")
    public boolean allocationInEffect;

    @Schema(description = "The most recently started Credit Allocation as of 'at' (valid_from on or before 'at'), whether or not it is still in effect. Null if no such allocation has ever been registered for this group.", nullable = true)
    @JsonProperty("basis_allocation")
    public BasisAllocationDto basisAllocation;

    @Schema(type = SchemaType.NUMBER, description = "The full total_credits of basis_allocation if it is in effect at 'at'; 0 otherwise.", example = "1000.0")
    @JsonProperty("allocated_credits")
    public double allocatedCredits;

    @Schema(type = SchemaType.NUMBER, description = "Credits consumed by this group across all installations under the project: from basis_allocation's valid_from up to 'at' if it is in effect; from its valid_to up to 'at' if it has expired; from the beginning of recorded history if there is no basis allocation.", example = "1150.0")
    @JsonProperty("consumed_credits")
    public double consumedCredits;

    @Schema(type = SchemaType.NUMBER, description = "allocated_credits minus consumed_credits. Negative means the group is spending in overdraft.", example = "-150.0")
    public double balance;

    @Schema(type = SchemaType.STRING, description = "Present only when balance is negative: 'allocated credits exhausted' if an allocation was in effect but fully consumed, or 'no allocation policy in effect' otherwise.", example = "allocated credits exhausted", nullable = true)
    public String reason;

    @Schema(description = "The per-installation, per-metric consumption breakdown backing consumed_credits.")
    public List<InstallationReportDto> installations;
}