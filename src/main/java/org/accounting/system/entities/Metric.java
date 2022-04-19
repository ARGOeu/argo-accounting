package org.accounting.system.entities;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.time.Instant;

/**
 * The Metric class represents the Metric collection stored in the mongo database.
 * Every instance of this class represents a record in that collection.
 */

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Metric extends Entity {



    private ObjectId id;
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
    @EqualsAndHashCode.Include
    @BsonProperty("value")
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