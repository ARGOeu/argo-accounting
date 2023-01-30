package org.accounting.system.entities.projections.normal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(name="ProjectResponse", description="An object represents a Project containing the Providers and Installations assigned to it.")
@JsonPropertyOrder({"id", "acronym", "title", "start_date", "end_date", "callIdentifier", "providers" })
public class ProjectProjection {

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
            implementation = ProviderProjection.class,
            description = "The Providers associated with a Project."
    )
    @JsonProperty("providers")
    public List<ProviderProjection> providers;

    public ProjectProjection(){

        this.providers = new ArrayList<>();
    }

    public String getId() {
        return id;
    }
}
