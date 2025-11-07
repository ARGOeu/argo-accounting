package org.accounting.system.entities.projections;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.accounting.system.entities.Capacity;
import org.accounting.system.entities.MetricDefinition;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InstallationProjection {

    private String id;

    private String project;

    private String organisation;

    @EqualsAndHashCode.Include
    private String infrastructure;

    @EqualsAndHashCode.Include
    private String installation;

    private MetricDefinition metricDefinition;

    @BsonProperty("unit_of_access")
    private List<MetricDefinition> metricDefinitions;

    private String resource;

    @BsonProperty("external_id")
    private String externalId;

    @Getter
    @Setter
    @BsonProperty("capacities")
    private List<Capacity> capacities;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(String infrastructure) {
        this.infrastructure = infrastructure;
    }

    public String getInstallation() {
        return installation;
    }

    public void setInstallation(String installation) {
        this.installation = installation;
    }

    public List<MetricDefinition> getMetricDefinitions() {
        return metricDefinitions;
    }

    public void setMetricDefinitions(List<MetricDefinition> metricDefinitions) {
        this.metricDefinitions = metricDefinitions;
    }

    public MetricDefinition getMetricDefinition() {

        if(metricDefinitions.isEmpty()){

            return null;
        } else {

            return metricDefinitions.get(0);
        }
    }

    public void setMetricDefinition(MetricDefinition metricDefinition) {
        this.metricDefinition = metricDefinition;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
