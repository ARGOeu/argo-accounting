package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

/**
 * The Metric Definition class represents the MetricDefinition collection stored in the mongo database.
 * Every instance of this class represents a record in that collection.
 */

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class MetricDefinition extends Entity {

    private ObjectId id;
    @EqualsAndHashCode.Include
    private String metricName;
    private String metricDescription;
    @EqualsAndHashCode.Include
    private String unitType;
    private String metricType;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName.toLowerCase();
    }

    public String getMetricDescription() {
        return metricDescription;
    }

    public void setMetricDescription(String metricDescription) {
        this.metricDescription = metricDescription;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}
