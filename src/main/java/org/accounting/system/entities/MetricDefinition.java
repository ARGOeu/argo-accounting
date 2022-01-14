package org.accounting.system.entities;

import org.bson.types.ObjectId;

/**
 * The Metric Definition entity represents the MetricDefinition collection stored in the mongo database.
 * Every instance of this entity represents a record in that collection.
 */
public class MetricDefinition {

    private ObjectId id;
    private String metricName;
    private String metricDescription;
    private String unitType;
    private String metricType;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

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
        this.unitType = unitType.toLowerCase();
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }
}
