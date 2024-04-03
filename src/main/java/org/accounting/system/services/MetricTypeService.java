package org.accounting.system.services;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.metrictype.MetricTypeDto;
import org.accounting.system.dtos.metrictype.UpdateMetricTypeRequestDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.endpoints.MetricTypeEndpoint;
import org.accounting.system.entities.MetricType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.MetricTypeMapper;
import org.accounting.system.repositories.metrictype.MetricTypeRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import java.util.Optional;

/**
 * This service exposes business logic, which uses the {@link MetricTypeRepository}.
 * It is used to keep logic to a minimum in {@link MetricTypeEndpoint} and
 * {@link MetricTypeRepository}
 */
@ApplicationScoped
public class MetricTypeService {

    @Inject
    MetricTypeRepository metricTypeRepository;

    @Inject
    MetricDefinitionService metricDefinitionService;

    /**
     * Maps the {@link MetricTypeDto incoming request} to {@link MetricType database entity}.
     * Then the transformed {@link MetricType entity} is stored in the mongo database.
     *
     * @param request The POST request body.
     * @return The stored Metric Type has been turned into a response body.
     */
    public MetricTypeDto save(MetricTypeDto request) {

        var optional = getMetricByType(request.metricType);

        optional.ifPresent(mt -> {throw new ConflictException("This Metric Type already exists in the Accounting Service database. Its id is "+mt.getId().toString());});

        var metricType = MetricTypeMapper.INSTANCE.requestToMetricType(request);

        metricTypeRepository.save(metricType);

        return MetricTypeMapper.INSTANCE.metricTypeToResponse(metricType);
    }

    /**
     * Deletes an existing Metric Type by given id.
     * @param id The Metric Type to be deleted.
     * @return If the operation is successful or not.
     * @throws ForbiddenException If the given Metric Type is used in a Metric Definition.
     */
    public boolean delete(String id){

        var metricType = metricTypeRepository.findById(new ObjectId(id));

        if(StringUtils.isEmpty(metricType.getCreatorId())){
            throw new ForbiddenException("You cannot delete a Metric Type registered by Accounting Service.");
        }

        var isUsed = metricDefinitionService.metricTypeUsedInMetricDefinition(metricType.getMetricType());

        if(isUsed){
            throw new ForbiddenException("This Metric Type is used in an existing Metric Definition, so you cannot delete it.");
        }

        return metricTypeRepository.deleteEntityById(new ObjectId(id));
    }

    /**
     * This method returns a specific entity by given metric type. If the Metric Type does not exist, the Optional value is empty.
     *
     * @param metricType The Metric Type to be retrieved.
     * @return The wrapped Metric Type as Optional.
     */
    public Optional<MetricType> getMetricByType(String metricType){

       return metricTypeRepository.fetchMetricByType(metricType);
    }

    /**
     * This method fetches a specific entity by given id and turns it into a response body.
     *
     * @param id The Metric Type ID to be retrieved.
     * @return The wrapped Metric Type as Optional.
     */
    public MetricTypeDto getMetricTypeById(String id){

        var entity = metricTypeRepository.findById(new ObjectId(id));
        return MetricTypeMapper.INSTANCE.metricTypeToResponse(entity);
    }

    /**
     * Returns the N Metric Types from the given page.
     *
     * @param page Indicates the page number.
     * @param size The number of Metric Types to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<MetricTypeDto> findAllMetricTypesPageable(int page, int size, UriInfo uriInfo){

        PanacheQuery<MetricType> panacheQuery = metricTypeRepository.findAllPageable(page, size);

        return new PageResource<>(panacheQuery, MetricTypeMapper.INSTANCE.metricTypesToResponse(panacheQuery.list()), uriInfo);
    }

    /**
     * This method calls the {@link MetricTypeRepository metricTypeRepository} to update an existing Metric Type.
     *
     * @param id The Metric Type to be updated.
     * @param request The Metric Type attributes that have been requested to be updated.
     * @return The updated Metric Type.
     * @throws ConflictException If the Metric Type to be updated exists
     */
    public MetricTypeDto update(String id, UpdateMetricTypeRequestDto request){

        MetricType metricType = null;

        metricType = metricTypeRepository.updateEntity(new ObjectId(id), request);

        return MetricTypeMapper.INSTANCE.metricTypeToResponse(metricType);
    }
}
