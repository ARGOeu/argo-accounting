package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class UnitType extends Entity{

    private ObjectId id;

    @EqualsAndHashCode.Include
    private String unit;

    private String description;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}
