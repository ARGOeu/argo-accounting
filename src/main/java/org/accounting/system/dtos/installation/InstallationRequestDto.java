package org.accounting.system.dtos.installation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="InstallationRequest", description="An object represents a request for creating a new Installation.")
public class
InstallationRequestDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "It must point to a Project ID that has been either registered through the OpenAire or Accounting System API.",
            example = "447535",
            required = true
    )
    @JsonProperty("project")
    public String project;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "It must point to a Provider ID that has been either registered through the EOSC-Portal or Accounting System API.",
            example = "grnet",
            required = true
    )
    @JsonProperty("organisation")
    public String organisation;

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
            example = "GRNET-KNS",
            required = true
    )
    @JsonProperty("installation")
    public String installation;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unit of access. It must point to an existing Metric Definition. Obviously, you can add " +
                    "different Metrics to an Installation, but this attribute expresses the primary Unit of Access.",
            example = "507f1f77bcf86cd799439011",
            required = true
    )
    @JsonProperty("unit_of_access")
    public String unitOfAccess;
}
