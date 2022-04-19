package org.accounting.system.services;

import org.accounting.system.dtos.MetricRequestDto;
import org.accounting.system.dtos.MetricResponseDto;
import org.accounting.system.dtos.UpdateMetricRequestDto;
import org.accounting.system.entities.Metric;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.MetricRepository;
import org.accounting.system.util.QueryParser;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.parser.ParseException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * This service exposes business logic, which uses the {@link MetricRepository}.
 * It is used to keep logic to a minimum in {@link org.accounting.system.endpoints.MetricEndpoint} and
 * {@link MetricRepository}
 */
@ApplicationScoped
public class MetricService {

    @Inject
    MetricRepository metricRepository;

    @Inject
    MetricDefinitionService metricDefinitionService;

    @Inject
    QueryParser queryParser;
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
     * @throws NotFoundException If the Metric Definition doesn't exist
     */
    public MetricResponseDto save(MetricRequestDto request) {

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

    /**
     * Fetches a Metric by given metric id. Afterwards,
     * if the {@link Metric} exists, it turns it into {@link MetricResponseDto}.
     *
     * @param metricId
     * @return  metric response body
     */
    public MetricResponseDto fetchMetric(String metricId){

        var metric = findById(metricId).get();

        return MetricMapper.INSTANCE.metricToResponse(metric);
    }

    /**
     * Delete a Metric by given id.
     * @param metricId The Metric to be deleted
     * @return if the operation is successful or not
     * @throws NotFoundException If the Metric doesn't exist
     */
    public boolean delete(String metricId){

        return metricRepository.deleteById(new ObjectId(metricId));
    }

    /**
     * This method is responsible for updating a part or all attributes of existing Metric.
     *
     * @param id The Metric to be updated.
     * @param request The Metric attributes to be updated
     * @return The updated Metric
     * @throws NotFoundException If the Metric/Metric Definition doesn't exist
     */
    public MetricResponseDto update(String id, UpdateMetricRequestDto request){

        var metric = findById(id).get();

        //TODO We have to reconstruct better the conditions below

        if(request.start !=null && request.end ==null && Instant.parse(request.start).isAfter(metric.getEnd())){
            throw new BadRequestException("Timestamp of the starting date time cannot be after of Timestamp of the end date time.");
        }

        if(request.end !=null && request.start==null && Instant.parse(request.end).isBefore(metric.getStart())){
            throw new BadRequestException("Timestamp of the end date time cannot be before of Timestamp of the starting date time.");
        }

        if(request.end !=null && request.start!=null && Instant.parse(request.start).isAfter(Instant.parse(request.end))){
            throw new BadRequestException("Timestamp of the starting date time cannot be after of Timestamp of the end date time.");
        }

        if(request.end !=null && request.start!=null && Instant.parse(request.start).equals(Instant.parse(request.end))){
            throw new BadRequestException("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.");
        }

        if(request.end !=null && request.start!=null && Instant.parse(request.start).isAfter(Instant.parse(request.end))){
            throw new BadRequestException("Timestamp of the starting date time cannot be after of Timestamp of the end date time.");
        }

        if(request.start !=null && request.end ==null && Instant.parse(request.start).equals(metric.getEnd())){
            throw new BadRequestException("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.");
        }

        if(request.end !=null && request.start ==null && Instant.parse(request.end).equals(metric.getStart())){
            throw new BadRequestException("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.");
        }

        MetricMapper.INSTANCE.updateMetricFromDto(request, metric);

        metricRepository.update(metric);

        return MetricMapper.INSTANCE.metricToResponse(metric);
    }

    /**
     * Fetches a Metric by given id.
     *
     * @param id The Metric id
     * @return Optional of Metric
     */
    public Optional<Metric> findById(String id){

        return metricRepository.findByIdOptional(new ObjectId(id));
    }

    public List<MetricResponseDto> searchMetric(String json) throws ParseException, NoSuchFieldException {

        Bson query=queryParser.parseFile(json);

        var list = metricRepository.search(query);
        return  MetricMapper.INSTANCE.metricsToResponse( list);
    }

}
