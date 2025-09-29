package org.accounting.system.entities.projections;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(name="InstallationReport", description="A report for a specific installation that includes aggregated metric values over a time period.")
public class InstallationReport {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The installation ID.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("installation_id")
    public String installationId;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Project that this Installation belongs to.",
            example = "447535"
    )
    @JsonProperty("project")
    public String project;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Provider that this Installation belongs to.",
            example = "grnet"
    )
    @JsonProperty("provider")
    public String provider;

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
            description = "Short name of Infrastructure.",
            example = "okeanos-knossos"
    )
    @JsonProperty("infrastructure")
    public String infrastructure;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Resource ID.",
            example = "unitartu.ut.rocket"
    )
    @JsonProperty("resource")
    public String resource;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The external installation ID.",
            example = "installation-45583"
    )
    @JsonProperty("external_id")
    public String externalId;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = MetricReportProjection.class,
            description = "List of aggregated metrics by Metric Definition."
    )
    @JsonProperty("data")
    public List<MetricReportProjection> data;
}
