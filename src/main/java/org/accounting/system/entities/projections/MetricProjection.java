package org.accounting.system.entities.projections;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Schema(name="Metric", description="An object represents the stored Metric. It also contains the hierarchical structure in which it belongs.")
public class MetricProjection {

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Unique ID of the metric.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("id")
    private ObjectId id;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Metric Definition ID.",
            example = "507f1f77bcf86cd799439011"
    )
    @JsonProperty("metric_definition_id")
    @BsonProperty("metric_definition_id")
    private String metricDefinitionId;

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the starting date time.",
            example = "2022-01-05T09:13:07Z"
    )
    @JsonProperty("time_period_start")
    @BsonProperty("time_period_start")
    private Instant start;

    @Schema(
            type = SchemaType.STRING,
            implementation = Instant.class,
            description = "Timestamp of the end date time.",
            example = "2022-01-05T09:13:07Z"
    )
    @JsonProperty("time_period_end")
    @BsonProperty("time_period_end")
    private Instant end;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Double.class,
            description = "Value of the metric for the given period.",
            example = "10.5"
    )
    @JsonProperty("value")
    @BsonProperty("value")
    private double value;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Project Acronym.",
            example = "702645"
    )
    @JsonProperty("project")
    private String project;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Provider ID.",
            example = "grnet"
    )
    @JsonProperty("provider")
    private String provider;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Installation name.",
            example = "GRNET-KNS"
    )
    @JsonProperty("installation")
    private String installation;

    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Infrastructure name.",
            example = "Okeanos-knossos"
    )
    @JsonProperty("infrastructure")
    private String infrastructure;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getMetricDefinitionId() {
        return metricDefinitionId;
    }

    public void setMetricDefinitionId(String metricDefinitionId) {
        this.metricDefinitionId = metricDefinitionId;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getInstallation() {
        return installation;
    }

    public void setInstallation(String installation) {
        this.installation = installation;
    }

    public String getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(String infrastructure) {
        this.infrastructure = infrastructure;
    }
}
