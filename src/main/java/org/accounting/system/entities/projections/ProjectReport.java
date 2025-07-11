package org.accounting.system.entities.projections;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.dtos.acl.role.RoleAccessControlResponseDto;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(name="ProjectReport", description="A report for a specific Project that includes aggregated metric values over a time period and associated role-based access control settings.")
public class ProjectReport {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Project ID.",
            example = "447535"
    )
    @JsonProperty("id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Project acronym.",
            example = "EGI-ACE"
    )
    @JsonProperty("acronym")
    public String acronym;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "A brief description of the Project.",
            example = "EGI Advanced Computing for EOSC."
    )
    @JsonProperty("title")
    public String title;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Project start date.",
            example = "2018-12-31"
    )
    @JsonProperty("start_date")
    public String startDate;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Project end date.",
            example = "2023-06-30"
    )
    @JsonProperty("end_date")
    public String endDate;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Project call identifier.",
            example = "H2020-EINFRA-2017."
    )
    @JsonProperty("call_identifier")
    public String callIdentifier;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = ProviderReport.class,
            description = "List of aggregated metrics by Provider."
    )
    @JsonProperty("data")
    public List<ProviderReport> data;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = RoleAccessControlResponseDto.class,
            description = "Role-based access control configuration for the project."
    )
    @JsonProperty("permissions")
    public List<RoleAccessControlResponseDto> permissions;

}
