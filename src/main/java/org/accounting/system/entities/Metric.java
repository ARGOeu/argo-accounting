package org.accounting.system.entities;

import java.time.Instant;

/**
 * The Metric class represents the Metric collection stored in the mongo database.
 * Every instance of this class represents a record in that collection.
 */
public class Metric extends Entity {

    private String resourceId;
    private String metricDefinitionId;
    private Instant start;
    private Instant end;
    private double value;

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