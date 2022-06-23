package org.accounting.system.repositories.metricdefinition;

import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import org.accounting.system.dtos.metricdefinition.UpdateMetricDefinitionRequestDto;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.entities.acl.PermissionAccessControl;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.repositories.modulators.AbstractModulator;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import javax.inject.Inject;


public class MetricDefinitionModulator extends AbstractModulator<MetricDefinition, ObjectId, PermissionAccessControl> {


    @Inject
    MetricDefinitionAccessEntityRepository metricDefinitionAccessEntityRepository;

    @Inject
    MetricDefinitionAccessAlwaysRepository metricDefinitionAccessAlwaysRepository;

    /**
     * This method is responsible for updating a part or all attributes of existing Metric Definition.
     *
     * @param id The Metric Definition to be updated.
     * @param request The Metric Definition attributes to be updated.
     * @return The updated Metric Definition.
     */
    public MetricDefinition updateEntity(ObjectId id, UpdateMetricDefinitionRequestDto request) {

        MetricDefinition entity = findById(id);

        MetricDefinitionMapper.INSTANCE.updateMetricDefinitionFromDto(request, entity);

        if(!StringUtils.isAllBlank(request.metricName, request.unitType)){
            exist(entity.getUnitType(), entity.getMetricName());
        }

        return super.updateEntity(entity, id);
    }

    public void exist(String unitType, String name){

        var optional = find("unitType = ?1 and metricName = ?2", unitType, name)
                .withCollation(Collation.builder().locale("en")
                        .collationStrength(CollationStrength.SECONDARY).build())
                .stream().findAny();

        optional.ifPresent(metricDefinition -> {throw new ConflictException("There is a Metric Definition with unit type "+unitType+" and name "+name+". Its id is "+metricDefinition.getId().toString());});
    }

    @Override
    public MetricDefinitionAccessAlwaysRepository always() {
        return metricDefinitionAccessAlwaysRepository;
    }

    @Override
    public MetricDefinitionAccessEntityRepository entity() {
        return metricDefinitionAccessEntityRepository;
    }
}