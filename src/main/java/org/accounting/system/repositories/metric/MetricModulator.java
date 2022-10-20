package org.accounting.system.repositories.metric;

import org.accounting.system.dtos.metric.UpdateMetricRequestDto;
import org.accounting.system.entities.Metric;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.modulators.AbstractModulator;
import org.bson.types.ObjectId;

import javax.inject.Inject;


public class MetricModulator extends AbstractModulator<Metric, ObjectId> {


    @Inject
    MetricAccessEntityRepository metricAccessEntityRepository;

    @Inject
    MetricAccessAlwaysRepository metricAccessAlwaysRepository;

    /**
     * This method is responsible for updating a part or all attributes of existing Metric.
     *
     * @param id The Metric to be updated.
     * @param request The Metric attributes to be updated.
     * @return The updated Metric.
     */
    public Metric updateEntity(ObjectId id, UpdateMetricRequestDto request) {

        Metric entity = findById(id);

        MetricMapper.INSTANCE.updateMetricFromDto(request, entity);

        super.update(entity);

        return entity;
    }

    @Override
    public MetricAccessAlwaysRepository always() {
        return metricAccessAlwaysRepository;
    }

    @Override
    public MetricAccessEntityRepository entity() {
        return metricAccessEntityRepository;
    }

}