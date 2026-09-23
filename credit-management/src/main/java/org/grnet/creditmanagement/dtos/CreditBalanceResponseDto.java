package org.grnet.creditmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@JsonPropertyOrder({ "project_id", "group_id", "at", "is_snapshot", "warning", "basis_allocation",
        "allocated_credits", "consumed_credits", "balance", "reason", "installations" })
public class CreditBalanceResponseDto {

    @Schema(type = SchemaType.STRING, description = "The project id.", example = "707f1f77bcf86cd799439011")
    @JsonProperty("project_id")
    public String projectId;

    @Schema(type = SchemaType.STRING, description = "The group id.", example = "group-42")
    @JsonProperty("group_id")
    public String groupId;

    @Schema(type = SchemaType.STRING, description = "The point in time this balance was computed as of.", example = "2026-08-20T10:00:00Z")
    public Instant at;

    @Schema(type = SchemaType.BOOLEAN, description = "True when 'at' was explicitly provided in the request rather than defaulting to the current moment.", example = "true")
    @JsonProperty("is_snapshot")
    public boolean isSnapshot;

    @Schema(type = SchemaType.STRING, description = "Present only when is_snapshot is true: this balance is a historical snapshot as of 'at' and says nothing about the current (true) balance.", nullable = true)
    public String warning;

    @Schema(description = "The Credit Allocation this balance is based on: the most recently started allocation whose valid_from is on or before 'at'. Null if no such allocation has ever been registered for this group as of 'at'.", nullable = true)
    @JsonProperty("basis_allocation")
    public BasisAllocationDto basisAllocation;

    @Schema(type = SchemaType.NUMBER, description = "The full total_credits of basis_allocation, or 0 if there is no basis_allocation.", example = "1000.0")
    @JsonProperty("allocated_credits")
    public double allocatedCredits;

    @Schema(type = SchemaType.NUMBER, description = "Credits consumed by this group across all installations under the project, from basis_allocation's valid_from (or the earliest recorded event, if there is no basis_allocation) up to 'at'.", example = "1150.0")
    @JsonProperty("consumed_credits")
    public double consumedCredits;

    @Schema(type = SchemaType.NUMBER, description = "allocated_credits minus consumed_credits. Negative means the group is spending in overdraft.", example = "-150.0")
    public double balance;

    @Schema(type = SchemaType.STRING, description = "Present only when balance is negative: 'allocated credits exhausted' if basis_allocation was still in effect at 'at' but its budget was fully consumed, or 'no allocation policy in effect' if no allocation covered 'at' at all.", example = "allocated credits exhausted", nullable = true)
    public String reason;

    @Schema(description = "The per-installation, per-metric consumption breakdown backing consumed_credits.")
    public List<InstallationReportDto> installations;
}