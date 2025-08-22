package org.accounting.system.dtos.installation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="InstallationResponse", description="An object represents the stored Installation.")
public class InstallationResponseDto {

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
            description = "The Project that this Installation belongs to.",
            example = "447535",
            required = true
    )
    @JsonProperty("project")
    public String project;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider that this Installation belongs to.",
            example = "grnet"
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
            description = "Unique external Installation identifier.",
            example = "installation-446655440000"
    )
    @JsonProperty("external_id")
    public String externalId;

    @Schema(
            type = SchemaType.OBJECT,
            implementation = MetricDefinitionResponseDto.class,
            description = "The primary Metric Registration.")
    @JsonProperty("unit_of_access")
    public MetricDefinitionResponseDto metricDefinition;
}
