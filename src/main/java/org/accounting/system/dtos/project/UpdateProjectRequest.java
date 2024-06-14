package org.accounting.system.dtos.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.constraints.CheckDateFormat;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="UpdateProjectRequest", description="An object represents a request for updating a Project.")
public class UpdateProjectRequest {

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
    @CheckDateFormat(pattern = "yyyy-mm-dd", message = "Valid date format is yyyy-mm-dd.")
    @JsonProperty("start_date")
    public String startDate;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Project end date.",
            example = "2023-06-30"
    )
    @CheckDateFormat(pattern = "yyyy-mm-dd", message = "Valid date format is yyyy-mm-dd.")
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
}
