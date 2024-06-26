package org.accounting.system.entities.projections;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.List;


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
            description = "Installation ID.",
            example = "62b30abeed03ea0023b9c6f5"
    )
    @JsonProperty("installation_id")
    @BsonProperty("installation_id")
    private String installationId;
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Project ID.",
            example = "111111"
    )

    @JsonProperty("project_id")
    @BsonProperty("project_id")
    private String projectId;

    @JsonProperty("resource")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @BsonProperty("resource")
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "The Resource ID.",
            example = "unitartu.ut.rocket"
    )
    private String resource;

    @JsonProperty("group_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @BsonProperty("group_id")
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "Group ID associated with a metric.",
            example = "Group ID"
    )
    private String groupId;

    @JsonProperty("user_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @BsonProperty("user_id")
    @Schema(
            type = SchemaType.STRING,
            implementation = String.class,
            description = "User ID associated with a metric.",
            example = "User ID"
    )
    private String userId;

    @JsonProperty("metric_definition")
    @Schema(
            type = SchemaType.OBJECT,
            implementation = MetricDefinitionResponseDto.class,
            description = "The Metric Definition the Metric refer to."
    )
    private MetricDefinitionResponseDto metricDefinitionResponseDto;

    @BsonProperty("metric_definition")
    @JsonIgnore
    private List<MetricDefinition> metricDefinitions;

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public MetricDefinitionResponseDto getMetricDefinitionResponseDto() {
        metricDefinitionResponseDto = MetricDefinitionMapper.INSTANCE.metricDefinitionToResponse(metricDefinitions.get(0));
        return metricDefinitionResponseDto;
    }

    public void setMetricDefinitionResponseDto(MetricDefinitionResponseDto metricDefinitionResponseDto) {
        this.metricDefinitionResponseDto = metricDefinitionResponseDto;
    }

    public List<MetricDefinition> getMetricDefinitions() {
        return metricDefinitions;
    }

    public void setMetricDefinitions(List<MetricDefinition> metricDefinitions) {
        this.metricDefinitions = metricDefinitions;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

 public String getGroupId() {
  return groupId;
 }

 public void setGroupId(String groupId) {
  this.groupId = groupId;
 }

 public String getUserId() {
  return userId;
 }

 public void setUserId(String userId) {
  this.userId = userId;
 }
}
