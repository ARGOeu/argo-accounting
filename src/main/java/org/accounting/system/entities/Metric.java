package org.accounting.system.entities;

import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * The Metric entity represents the Metric collection stored in the mongo database.
 * Every instance of this entity represents a record in that collection.
 */
public class Metric {

    private ObjectId id;
    private String resourceId;
    private String metricDefinitionId;
    private Instant start;
    private Instant end;
    private double value;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

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
}