package org.accounting.system.dtos.installation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="UpdateInstallationRequest", description="An object represents a request for updating an existing Installation.")
public class UpdateInstallationRequestDto {
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
            description = "Unit of access. It must point to an existing Metric Definition. Obviously, you can add " +
                    "different Metrics to an Installation, but this attribute expresses the primary Unit of Access.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("unit_of_access")
    @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:")
    public String unitOfAccess;
}
