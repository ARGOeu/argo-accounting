package org.accounting.system.services;

import org.accounting.system.dtos.MetricRequestDto;
import org.accounting.system.dtos.MetricResponseDto;
import org.accounting.system.entities.Metric;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.MetricRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * This service exposes business logic, which uses the {@link MetricRepository}.
 * It is used to keep logic to a minimum in {@link org.accounting.system.endpoints.MetricEndpoint} and
 * {@link MetricRepository}
 */
@ApplicationScoped
public class MetricService {

    @Inject
    private MetricRepository metricRepository;

    @Inject
    private MetricDefinitionService metricDefinitionService;

    public MetricService(MetricRepository metricRepository, MetricDefinitionService metricDefinitionService) {
        this.metricRepository = metricRepository;
        this.metricDefinitionService = metricDefinitionService;
    }

    /**
     * Maps the {@link MetricRequestDto} to {@link Metric}.
     * Then the {@link Metric} is stored in the mongo database.
     *
     * @param request The POST request body
     * @return The stored metric has been turned into a response body
     */
    public MetricResponseDto save(MetricRequestDto request) {

        metricDefinitionService.findByIdOrThrowException(request.metricDefinitionId);

        Metric metric = MetricMapper.INSTANCE.requestToMetric(request);

        metricRepository.persist(metric);

        return MetricMapper.INSTANCE.metricToResponse(metric);
    }

    /**
     * Passing the Metric Definition id, you can retrieve the total number of metrics assigned to it.
     *
     * @param metricDefinitionId
     * @return the number of the Metrics assigned to the given metric definition id
     */
    public long countMetricsByMetricDefinitionId(String metricDefinitionId){

        return metricRepository.countMetricsByMetricDefinitionId(metricDefinitionId);
    }
}
