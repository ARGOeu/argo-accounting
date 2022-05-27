package org.accounting.system.dtos.installation;

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
    @JsonProperty("installation_id")
    public String id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Organisation short name.",
            example = "GRNET"
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
            description = "The primary Metric Registration.",
            example = "{\n" +
                    "    \"metric_definition_id\": \"628ddb672f926e16a7a74156\",\n" +
                    "    \"metric_name\": \"test5\",\n" +
                    "    \"metric_description\": \"storage\",\n" +
                    "    \"unit_type\": \"#\",\n" +
                    "    \"metric_type\": \"aggregated\"\n" +
                    "}")
    @JsonProperty("unit_of_access")
    public MetricDefinitionResponseDto metricDefinition;
}
