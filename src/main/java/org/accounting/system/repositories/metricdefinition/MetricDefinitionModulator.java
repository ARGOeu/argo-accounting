package org.accounting.system.repositories.metricdefinition;

import org.accounting.system.dtos.metricdefinition.UpdateMetricDefinitionRequestDto;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.repositories.modulators.AbstractModulator;
import org.bson.types.ObjectId;

import javax.inject.Inject;


public class MetricDefinitionModulator extends AbstractModulator<MetricDefinition, ObjectId> {


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

        return super.updateEntity(entity, id);
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