package org.accounting.system.dtos.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.accounting.system.constraints.CheckDateFormat;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="ProjectRequest", description="This object represents a request for creating a new Project.")
public class ProjectRequest {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Project ID.",
            required = true,
            example = "447535"
    )
    @NotEmpty(message = "id may not be empty.")
    @JsonProperty("id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Project acronym.",
            required = true,
            example = "EGI-ACE"
    )
    @JsonProperty("acronym")
    @NotEmpty(message = "acronym may not be empty.")
    public String acronym;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "A brief description of the Project.",
            required = true,
            example = "EGI Advanced Computing for EOSC."
    )
    @JsonProperty("title")
    @NotEmpty(message = "title may not be empty.")
    public String title;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Project start date.",
            required = true,
            example = "2018-12-31"
    )
    @JsonProperty("start_date")
    @NotEmpty(message = "start_date may not be empty.")
    @CheckDateFormat(pattern = "yyyy-mm-dd", message = "Valid date format is yyyy-mm-dd.")
    public String startDate;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Project end date.",
            required = true,
            example = "2023-06-30"
    )
    @JsonProperty("end_date")
    @NotEmpty(message = "end_date may not be empty.")
    @CheckDateFormat(pattern = "yyyy-mm-dd", message = "Valid date format is yyyy-mm-dd.")
    public String endDate;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Project call identifier.",
            required = true,
            example = "H2020-EINFRA-2017."
    )
    @JsonProperty("call_identifier")
    @NotEmpty(message = "call_identifier may not be empty.")
    public String callIdentifier;
}
