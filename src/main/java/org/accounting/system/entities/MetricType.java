package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MetricType extends Entity{

    private ObjectId id;

    @EqualsAndHashCode.Include
    private String metricType;

    private String description;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
