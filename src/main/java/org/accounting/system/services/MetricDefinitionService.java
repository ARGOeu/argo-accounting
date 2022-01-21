package org.accounting.system.services;

import org.accounting.system.dtos.MetricDefinitionDtoRequest;
import org.accounting.system.dtos.MetricDefinitionDtoResponse;
import org.accounting.system.dtos.UpdateMetricDefinitionDtoRequest;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.repositories.MetricDefinitionRepository;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * This service exposes business logic, which uses the {@link MetricDefinitionRepository}.
 * It is used to keep logic to a minimum in {@link org.accounting.system.endpoints.MetricDefinitionEndpoint} and
 * {@link MetricDefinitionRepository}
 */
@ApplicationScoped
public class MetricDefinitionService {

    @Inject
    private MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    private MetricService metricService;


    public MetricDefinitionService(MetricDefinitionRepository metricDefinitionRepository, MetricService metricService) {
        this.metricDefinitionRepository = metricDefinitionRepository;
        this.metricService = metricService;
    }

    /**
     * Maps the {@link MetricDefinitionDtoRequest} to {@link MetricDefinition}.
     * Then the {@link MetricDefinition} is stored in the mongo database.
     *
     * @param request The POST request body
     * @return The stored metric definition has been turned into a response body
     */
    public MetricDefinitionDtoResponse save(MetricDefinitionDtoRequest request) {

        var metricDefinition = MetricDefinitionMapper.INSTANCE.requestToMetricDefinition(request);

        metricDefinitionRepository.persist(metricDefinition);

        return MetricDefinitionMapper.INSTANCE.metricDefinitionToResponse(metricDefinition);
    }

    public Optional<MetricDefinitionDtoResponse> fetchMetricDefinition(String id){

        var optionalMetricDefinition = metricDefinitionRepository.findByIdOptional(new ObjectId(id));

        return optionalMetricDefinition.map(MetricDefinitionMapper.INSTANCE::metricDefinitionToResponse).stream().findAny();
    }

    public List<MetricDefinitionDtoResponse> fetchAllMetricDefinitions(){

        var list = metricDefinitionRepository.findAll().list();

        return MetricDefinitionMapper.INSTANCE.metricDefinitionsToResponse(list);
    }

    public MetricDefinitionDtoResponse update(String id, UpdateMetricDefinitionDtoRequest request){

        var optionalMetricDefinition = metricDefinitionRepository.findByIdOptional(new ObjectId(id));

        var metricDefinition = optionalMetricDefinition.orElseThrow(()->new NotFoundException("The Metric Definition has not been found."));

        MetricDefinitionMapper.INSTANCE.updateMetricDefinitionFromDto(request, metricDefinition);

        metricDefinitionRepository.update(metricDefinition);

        return MetricDefinitionMapper.INSTANCE.metricDefinitionToResponse(metricDefinition);
    }

    /**
     * Delete a Metric Definition by given id.
     * @param metricDefinitionId
     * @return if the operation is successful or not
     * @throws NotFoundException If the Metric Definition doesn't exist
     */
    public boolean delete(String metricDefinitionId){

        var optionalMetricDefinition = metricDefinitionRepository.findByIdOptional(new ObjectId(metricDefinitionId));
        optionalMetricDefinition.orElseThrow(()->new NotFoundException("The Metric Definition has not been found."));

        return metricDefinitionRepository.deleteById(new ObjectId(metricDefinitionId));
    }

    /**
     *Τwo Metric Definitions are considered similar when having the same unit type and name.
     *
     * @param unitType Unit Type of the Metric
     * @param name The name of the Metric
     * @throws ConflictException If Metric Definition already exists
     */
    public void exist(String unitType, String name){

        metricDefinitionRepository.exist(unitType, name)
                .ifPresent(metricDefinition -> {throw new ConflictException("There is a Metric Definition with unit type "+metricDefinition.getUnitType()+" and name "+metricDefinition.getMetricName()+". Its id is "+metricDefinition.getId().toString());});
    }

    /**
     * Fetches a Metric Definition by given id.
     *
     * @param id The Metric Definition id
     * @throws NotFoundException If there is no Metric Definition with this id
     */
    public MetricDefinition findByIdOrThrowException(String id){

        Optional<MetricDefinition> optionalMetricDefinition = metricDefinitionRepository.findByIdOptional(new ObjectId(id));

        return optionalMetricDefinition.orElseThrow(()->new NotFoundException("There is no Metric Definition with the following id: "+id));
    }

    /**
     * Checks if there is any Metric assigned to the Metric Definition.
     *
     * @param id The Metric Definition id
     * @throws ConflictException If the Metric Definition has children
     */
    public void hasChildren(String id){

        if(metricService.countMetricsByMetricDefinitionId(id) > 0){
            throw new ConflictException("The Metric Definition cannot be deleted. There is a Metric assigned to it.");
        }
    }
}