package org.accounting.system.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Response object containing the count of documents in each collection.")
public class CountDocumentResponse {

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Long.class,
            description = "Number of documents in the MetricDefinition collection.",
            example = "120"
    )
    @JsonProperty("metric_definition_count")
    public long metricDefinitionCount;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Long.class,
            description = "Number of documents in the Metric collection.",
            example = "1200"
    )
    @JsonProperty("metric_count")
    public long metricCount;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Long.class,
            description = "Number of documents in the Actor.",
            example = "1200"
    )
    @JsonProperty("actor_count")
    public long actorCount;
}
