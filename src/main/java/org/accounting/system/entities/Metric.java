package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * The Metric class represents the Metric collection stored in the mongo database.
 * Every instance of this class represents a record in that collection.
 */

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
public class Metric extends Entity {

    private ObjectId id;
    @EqualsAndHashCode.Include
    @BsonProperty("resource_id")
    private String resourceId;
    @EqualsAndHashCode.Include
    @BsonProperty("metric_definition_id")
    private String metricDefinitionId;
    @EqualsAndHashCode.Include
    @BsonProperty("time_period_start")

    private Instant start;
    @EqualsAndHashCode.Include
    @BsonProperty("time_period_end")
    private Instant end;
    @BsonProperty("value")
    private double value;

    private String project;

    private String provider;

    private String installation;

    private String infrastructure;
    @BsonProperty("project_id")
    private String projectId;
    @BsonProperty("installation_id")
   private String installationId;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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

    public String getMetricDefinitionId() {
        return metricDefinitionId;
    }

    public void setMetricDefinitionId(String metricDefinitionId) {
        this.metricDefinitionId = metricDefinitionId;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }
}