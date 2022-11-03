package org.accounting.system.entities.projections.permissions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.accounting.system.dtos.authorization.CollectionAccessPermissionDto;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Schema(name="ProviderPermissionResponse", description="An object represents a Providers containing the Installations assigned to it. " +
        "It also encloses the client's permissions upon that Provider, if any.")
@JsonPropertyOrder({"id", "name", "website", "abbreviation", "logo", "project_id", "project_acronym", "permissions", "installations" })
public class ProviderProjectionWithPermissions {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider ID.",
            example = "sites"
    )
    @JsonProperty("id")
    public String id;
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider name.",
            example = "Swedish Infrastructure for Ecosystem Science"
    )
    @JsonProperty("name")
    public String name;
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider website.",
            example = "https://www.fieldsites.se/en-GB"
    )
    @JsonProperty("website")
    public String website;
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider abbreviation.",
            example = "SITES"
    )
    @JsonProperty("abbreviation")
    public String abbreviation;
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider logo.",
            example = "https://dst15js82dk7j.cloudfront.net/231546/95187636-P5q11.png"
    )
    @JsonProperty("logo")
    public String logo;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Project ID.",
            example = "447535"
    )
    @JsonProperty("project_id")
    @BsonProperty("project_id")
    public String projectId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Project acronym.",
            example = "EGI-ACE"
    )
    @JsonProperty("project_acronym")
    @BsonProperty("project_acronym")
    public String projectAcronym;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = ProjectionInstallationWithPermissions.class,
            description = "The Installation associated with a Provider."
    )
    @JsonProperty("installations")
    public List<ProjectionInstallationWithPermissions> installations;

    @JsonIgnore
    public List<RoleAccessControl> roleAccessControls;

    public Set<CollectionAccessPermissionDto> permissions;

    public ProviderProjectionWithPermissions(){

        this.installations = new ArrayList<>();
        this.roleAccessControls = new ArrayList<>();
        this.permissions = new HashSet<>();
    }
}
