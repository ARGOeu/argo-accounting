package org.accounting.system.dtos.installation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

@Schema(name="InstallationRequest", description="An object represents a request for creating a new Installation.")
public class
InstallationRequestDto {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "It must point to a Provider ID that has been either registered through the EOSC-Portal or Accounting System API.",
            example = "grnet",
            required = true
    )
    @JsonProperty("organisation")
    @NotEmpty(message = "organisation may not be empty.")
    @NotFoundEntity(repository = ProviderRepository.class, id = String.class, message = "There is no Provider with the following id:")
    public String organisation;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Short name of Infrastructure.",
            example = "okeanos-knossos",
            required = true
    )
    @JsonProperty("infrastructure")
    @NotEmpty(message = "infrastructure may not be empty.")
    public String infrastructure;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Short name of Installation.",
            example = "GRNET-KNS",
            required = true
    )
    @JsonProperty("installation")
    @NotEmpty(message = "installation may not be empty.")
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
    @NotEmpty(message = "unit_of_access may not be empty.")
    @NotFoundEntity(repository = MetricDefinitionRepository.class, message = "There is no Metric Definition with the following id:")
    public String unitOfAccess;
}
