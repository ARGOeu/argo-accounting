package org.accounting.system.entities.projections;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.accounting.system.dtos.permissions.CollectionAccessPermissionDto;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonPropertyOrder({"id", "acronym", "title", "start_date", "end_date", "callIdentifier", "permissions", "providers" })
public class ProjectProjectionWithPermissions {

    @Getter
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
            example = "2018-12-31."
    )
    @JsonProperty("start_date")
    @BsonProperty("start_date")
    public String startDate;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Project end date.",
            example = "2023-06-30."
    )
    @JsonProperty("end_date")
    @BsonProperty("end_date")
    public String endDate;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Project call identifier.",
            example = "H2020-EINFRA-2017."
    )
    @JsonProperty("call_identifier")
    @BsonProperty("call_identifier")
    public String callIdentifier;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = ProviderProjectionWithPermissions.class,
            description = "The Providers associated with a Project."
    )
    @JsonProperty("providers")
    public List<ProviderProjectionWithPermissions> providers;


    public Set<CollectionAccessPermissionDto> permissions;

    public ProjectProjectionWithPermissions(){

        this.providers = new ArrayList<>();
        this.permissions = new HashSet<>();
    }
}
