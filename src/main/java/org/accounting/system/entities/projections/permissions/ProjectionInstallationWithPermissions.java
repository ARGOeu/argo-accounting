package org.accounting.system.entities.projections.permissions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import org.accounting.system.dtos.authorization.CollectionAccessPermissionDto;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Schema(name="InstallationPermissionResponse", description="An object represents the stored Installation. " +
        "It also encloses the client's permissions upon that Installation, if any.")
@JsonPropertyOrder({"id", "infrastructure", "installation", "unit_of_access", "permissions" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProjectionInstallationWithPermissions {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Installation unique id.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("id")
    @EqualsAndHashCode.Include
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Short name of Infrastructure.",
            example = "okeanos-knossos"
    )
    @JsonProperty("infrastructure")
    public String infrastructure;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Short name of Installation.",
            example = "GRNET-KNS"
    )
    @JsonProperty("installation")
    public String installation;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The primary Metric Registration.",
            example = "628ddb672f926e16a7a74156")
    @JsonProperty("unit_of_access")
    @BsonProperty("unit_of_access")
    public ObjectId unitOfAccess;

    @JsonIgnore
    public List<RoleAccessControl> roleAccessControls;

    public Set<CollectionAccessPermissionDto> permissions;

    public ProjectionInstallationWithPermissions() {
        this.roleAccessControls = new ArrayList<>();
        this.permissions = new HashSet<>();
    }
}
