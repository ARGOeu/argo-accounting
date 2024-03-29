package org.accounting.system.entities.projections.normal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="InstallationProjectionResponse", description="An object represents the stored Installation.")
@JsonPropertyOrder({"id", "infrastructure", "installation", "resource", "unit_of_access" })
public class ProjectionInstallation {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Installation unique id.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("id")
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
            description = "The Resource ID.",
            example = "unitartu.ut.rocket"
    )
    @JsonProperty("resource")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String resource;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The primary Metric Registration.",
            example = "628ddb672f926e16a7a74156")
    @JsonProperty("unit_of_access")
    @BsonProperty("unit_of_access")
    public ObjectId unitOfAccess;
}
