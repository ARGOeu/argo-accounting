package org.accounting.system.repositories.metricdefinition;

import org.accounting.system.dtos.UpdateMetricDefinitionRequestDto;
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

    public MetricDefinition updateEntity(ObjectId id, UpdateMetricDefinitionRequestDto updateMetricDefinitionRequestDto) {

        MetricDefinition entity = findById(id);

        MetricDefinitionMapper.INSTANCE.updateMetricDefinitionFromDto(updateMetricDefinitionRequestDto, entity);

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
