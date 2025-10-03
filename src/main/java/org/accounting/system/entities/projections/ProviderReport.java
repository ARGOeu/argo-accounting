package org.accounting.system.entities.projections;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(name="ProviderReport", description="A report for a specific provider that includes aggregated metric values over a time period.")
public class ProviderReport {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The provider ID.",
            example = "327f1f77dcf86cd799439011"
    )
    @JsonProperty("provider_id")
    public String provider_id;

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
            description = "The provider external ID.",
            example = "grnet"
    )
    @JsonProperty("external_id")
    public String externalId;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = MetricReportProjection.class,
            description = "List of aggregated metrics at Provider level."
    )
    @JsonProperty("aggregated_metrics")
    public List<MetricReportProjection> aggregatedMetrics;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = InstallationReport.class,
            description = "List of aggregated metrics by Installation."
    )
    @JsonProperty("data")
    public List<InstallationReport> data;
}
