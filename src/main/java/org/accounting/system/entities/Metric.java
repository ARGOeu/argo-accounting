package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * The Metric class represents the Metric collection stored in the mongo database.
 * Every instance of this class represents a record in that collection.
 */

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Metric extends Entity {

    private ObjectId id;
    private String resourceId;
    @EqualsAndHashCode.Include
    private String metricDefinitionId;
    @EqualsAndHashCode.Include
    private Instant start;
    @EqualsAndHashCode.Include
    private Instant end;
    @EqualsAndHashCode.Include
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

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}