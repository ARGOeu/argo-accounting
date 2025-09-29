package org.accounting.system.entities.projections;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(name="GenericProviderReport", description="A report for a generic specific provider that includes aggregated metric values over a time period.")
public class GenericProviderReport {

    @Schema(
            type = SchemaType.ARRAY,
            implementation = ProviderReport.class,
            description = "List of aggregated metrics by Project."
    )
    @JsonProperty("reports")
    public List<ProviderReport> reports;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = MetricReportProjection.class,
            description = "List of aggregated metrics at Provider level."
    )
    @JsonProperty("aggregated_metrics")
    public List<MetricReportProjection> aggregatedMetrics;
}
