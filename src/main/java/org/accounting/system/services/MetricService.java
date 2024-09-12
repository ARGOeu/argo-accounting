package org.accounting.system.services;

import com.mongodb.MongoWriteException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.metric.UpdateMetricRequestDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.endpoints.MetricEndpoint;
import org.accounting.system.entities.Metric;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.util.QueryParser;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.parser.ParseException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This service exposes business logic, which uses the {@link MetricRepository}.
 * It is used to keep logic to a minimum in {@link MetricEndpoint} and
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

    @Inject
    InstallationRepository installationRepository;


    public MetricService(MetricRepository metricRepository, MetricDefinitionService metricDefinitionService) {
        this.metricRepository = metricRepository;
        this.metricDefinitionService = metricDefinitionService;
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

        var metric = metricRepository.findById(new ObjectId(metricId));

        return MetricMapper.INSTANCE.metricToResponse(metric);
    }

    /**
     * Delete a Metric by given id.
     * @param metricId The Metric to be deleted
     * @param installationId The Installation in which the Metric belongs to.
     * @return if the operation is successful or not
     * @throws NotFoundException If the Metric doesn't exist
     */
    public boolean delete(String metricId, String installationId){

        // first delete Metric from Metric Collection
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

        if((request.start!=null && Instant.parse(request.start).isAfter(Instant.now())) || (request.end!=null && Instant.parse(request.end).isAfter(Instant.now()))){
            throw new BadRequestException("Timestamp cannot be after the current date-time.");
        }

        if(request.start !=null && request.end ==null && Instant.parse(request.start).equals(metric.getEnd())){
            throw new BadRequestException("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.");
        }

        if(request.end !=null && request.start ==null && Instant.parse(request.end).equals(metric.getStart())){
            throw new BadRequestException("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.");
        }

        Metric updatedMetric = null;

        try{
            updatedMetric = metricRepository.updateEntity(new ObjectId(id), request);
        } catch (MongoWriteException e){
            throw new ConflictException("Metric already exists.");
        }

        return MetricMapper.INSTANCE.metricToResponse(updatedMetric);
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

    public PageResource<MetricProjection> searchMetrics(String json, int page, int size, UriInfo uriInfo) throws ParseException, NoSuchFieldException {
       Bson query=queryParser.parseFile(json,true,new ArrayList<>(),Metric.class);
        List<String> installationsIds=  installationRepository.fetchAllInstallationIds();

        var metrics=metricRepository.searchMetrics(query,installationsIds,page,size);
        return new PageResource<>(metrics, metrics.list(), uriInfo);
    }

    public PageResource<MetricProjection> fetchAllMetrics(String id, int page, int size, UriInfo uriInfo, String start, String end, String metricDefinitionId){

        var projection = metricRepository.findByExternalId(id, page, size, start, end, metricDefinitionId);

        return new PageResource<>(projection, projection.list(), uriInfo);
    }

    /**
     * Retrieves all metrics under a specific project related to a specific group id.
     *
     * @param projectId The ID of the project.
     * @param groupId The ID of the group.
     * @return A list of metrics related to the specified project and group.
     */
    public PageResource<MetricProjection> getMetricsByProjectAndGroup(String projectId, String groupId, int page, int size, String start, String end, UriInfo uriInfo){

        var projection = metricRepository.findByProjectIdAndGroupId(projectId, groupId, page, size, start, end);

        return new PageResource<>(projection, projection.list(), uriInfo);
    }

    /**
     * Retrieves all metrics under a specific project related to a specific user id.
     *
     * @param projectId The ID of the project.
     * @param userId The ID of the group.
     * @return A list of metrics related to the specified project and user.
     */
    public PageResource<MetricProjection> getMetricsByProjectAndUser(String projectId, String userId, int page, int size, String start, String end, UriInfo uriInfo){

        var projection = metricRepository.findByProjectIdAndUserId(projectId, userId, page, size, start, end);

        return new PageResource<>(projection, projection.list(), uriInfo);
    }
}
