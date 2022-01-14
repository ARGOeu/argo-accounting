package org.accounting.system.services;

import org.accounting.system.dtos.MetricDefinitionDtoRequest;
import org.accounting.system.dtos.MetricDefinitionDtoResponse;
import org.accounting.system.dtos.UpdateMetricDefinitionDtoRequest;
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

    public MetricDefinitionService(MetricDefinitionRepository metricDefinitionRepository) {
        this.metricDefinitionRepository = metricDefinitionRepository;
    }

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
     */
    public void exist(String unitType, String name){

        metricDefinitionRepository.exist(unitType, name)
                .ifPresent(metricDefinition -> {throw new ConflictException("There is a Metric Definition with unit type "+metricDefinition.getUnitType()+" and name "+metricDefinition.getMetricName()+". Its id is "+metricDefinition.getId().toString());});
    }

}