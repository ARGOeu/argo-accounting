package org.accounting.system.services;

import com.mongodb.MongoWriteException;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.dtos.acl.AccessControlRequestDto;
import org.accounting.system.dtos.acl.AccessControlResponseDto;
import org.accounting.system.dtos.acl.AccessControlUpdateDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.metricdefinition.UpdateMetricDefinitionRequestDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.endpoints.MetricDefinitionEndpoint;
import org.accounting.system.entities.Metric;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.enums.Collection;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.AccessControlMapper;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.acl.AccessControlRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.util.QueryParser;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.parser.ParseException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This service exposes business logic, which uses the {@link MetricDefinitionRepository}.
 * It is used to keep logic to a minimum in {@link MetricDefinitionEndpoint} and
 * {@link MetricDefinitionRepository}
 */
@ApplicationScoped
public class MetricDefinitionService {

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    @Inject
    MetricService metricService;

    @Inject
    MetricRepository metricRepository;

    @Inject

    AccessControlRepository accessControlRepository;
    @Inject
    QueryParser queryParser;

    public MetricDefinitionService(MetricDefinitionRepository metricDefinitionRepository, MetricService metricService, MetricRepository metricRepository, AccessControlRepository accessControlRepository) {
        this.metricDefinitionRepository = metricDefinitionRepository;
        this.metricService = metricService;
        this.metricRepository = metricRepository;
        this.accessControlRepository = accessControlRepository;
    }

    /**
     * Maps the {@link MetricDefinitionRequestDto} to {@link MetricDefinition}.
     * Then the {@link MetricDefinition} is stored in the mongo database.
     *
     * @param request The POST request body.
     * @return The stored metric definition has been turned into a response body.
     */
    public MetricDefinitionResponseDto save(MetricDefinitionRequestDto request) {

        var metricDefinition = MetricDefinitionMapper.INSTANCE.requestToMetricDefinition(request);

        metricDefinitionRepository.persist(metricDefinition);

        return MetricDefinitionMapper.INSTANCE.metricDefinitionToResponse(metricDefinition);
    }

    /**
     * Fetches a Metric Definition by given id.
     *
     * @param id The Metric Definition id.
     * @return The corresponding Metric Definition.
     */
    public MetricDefinitionResponseDto fetchMetricDefinition(String id){

        var metricDefinition = metricDefinitionRepository.fetchEntityById(new ObjectId(id));

        return MetricDefinitionMapper.INSTANCE.metricDefinitionToResponse(metricDefinition);
    }

    public List<MetricDefinitionResponseDto> fetchAllMetricDefinitions(){

        var list = metricDefinitionRepository.getAllEntities();

        return MetricDefinitionMapper.INSTANCE.metricDefinitionsToResponse(list);
    }



    /**
     * Returns the N Metric Definitions from the given page.
     *
     * @param page Indicates the page number.
     * @param size The number of Metric Definitions to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<MetricDefinition, MetricDefinitionResponseDto> findAllMetricDefinitionsPageable(int page, int size, UriInfo uriInfo){

        PanacheQuery<MetricDefinition> panacheQuery = metricDefinitionRepository.findAllPageable(page, size);

        return new PageResource<>(panacheQuery, MetricDefinitionMapper.INSTANCE.metricDefinitionsToResponse(panacheQuery.list()), uriInfo);
    }

    /**
     * This method calls the {@link MetricDefinitionRepository metricDefinitionRepository} to update a Metric Definition.
     *
     * @param id The Metric Definition to be updated.
     * @param request The Metric Definition attributes to be updated.
     * @return The updated Metric Definition.
     * @throws ConflictException If the Metric Definition with the given combination of unit_type and metric_name exists
     */
    public MetricDefinitionResponseDto update(String id, UpdateMetricDefinitionRequestDto request){

        MetricDefinition metricDefinition = null;

        try{
            metricDefinition = metricDefinitionRepository.updateEntity(new ObjectId(id), request);
        } catch (MongoWriteException e){
            throw new ConflictException("The combination of unit_type and metric_name should be unique. A Metric Definition with that combination has already been created.");
        }

        return MetricDefinitionMapper.INSTANCE.metricDefinitionToResponse(metricDefinition);
    }

    /**
     * Delete a Metric Definition by given id.
     * @param metricDefinitionId The Metric Definition to be deleted.
     * @return If the operation is successful or not.
     */
    public boolean delete(String metricDefinitionId){

        return metricDefinitionRepository.deleteEntityById(new ObjectId(metricDefinitionId));
    }

    /**
     *Τwo Metric Definitions are considered similar when having the same unit type and name.
     *
     * @param unitType Unit Type of the Metric.
     * @param name The name of the Metric.
     * @throws ConflictException If Metric Definition already exists.
     */
    public void exist(String unitType, String name){

        metricDefinitionRepository.exist(unitType, name)
                .ifPresent(metricDefinition -> {throw new ConflictException("There is a Metric Definition with unit type "+unitType+" and name "+name+". Its id is "+metricDefinition.getId().toString());});
    }

    /**
     * Checks if there is any Metric assigned to the Metric Definition.
     *
     * @param id The Metric Definition id.
     * @throws ConflictException If the Metric Definition has children.
     */
    public void hasChildren(String id){

        if(metricService.countMetricsByMetricDefinitionId(id) > 0){
            throw new ConflictException("The Metric Definition cannot be deleted. There is a Metric assigned to it.");
        }
    }

    /**
     * Returns the N Metrics, which have been assigned to the Metric Definition, from the given page.
     *
     * @param metricDefinitionId The Metric Definition id.
     * @param page Indicates the page number.
     * @param size The number of Metrics to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<Metric, MetricResponseDto>  findMetricsByMetricDefinitionIdPageable(String metricDefinitionId, int page, int size, UriInfo uriInfo){

        metricDefinitionRepository.fetchEntityById(new ObjectId(metricDefinitionId));

        PanacheQuery<Metric> panacheQuery = metricRepository.findMetricsByMetricDefinitionIdPageable(metricDefinitionId, page, size);

        return new PageResource<>(panacheQuery, MetricMapper.INSTANCE.metricsToResponse(panacheQuery.list()), uriInfo);
    }

    /**
     * Converts the request for {@link AccessControlRequestDto permissions} to {@link org.accounting.system.entities.acl.AccessControl} and stores it in the database.
     *
     * @param metricDefinitionId The metric definition to which permissions will be assigned.
     * @param who To whom the permissions will be granted.
     * @param request The permissions
     */
    public void grantPermission(String metricDefinitionId, String who, AccessControlRequestDto request){

        var accessControl = AccessControlMapper.INSTANCE.requestToAccessControl(request);

        accessControl.setEntity(metricDefinitionId);

        accessControl.setWho(who);

        accessControl.setCollection(Collection.MetricDefinition);

        metricDefinitionRepository.grantPermission(accessControl);
    }

    /**
     * Modifies permissions and stores them in the database.
     *
     * @param metricDefinitionId For which Metric Definition will the permissions be modified.
     * @param who To whom belongs the permissions which will be modified.
     * @param updateDto The permissions which will be modified.
     */
    public AccessControlResponseDto modifyPermission(String metricDefinitionId, String who, AccessControlUpdateDto updateDto){

        var accessControl = accessControlRepository.findByWhoAndCollectionAndEntity(who, Collection.MetricDefinition, metricDefinitionId);

        AccessControlMapper.INSTANCE.updateAccessControlFromDto(updateDto, accessControl);

        metricDefinitionRepository.modifyPermission(accessControl);

        return AccessControlMapper.INSTANCE.accessControlToResponse(accessControl);
    }

    /**
     * Deletes specific privileges from the database.
     *
     * @param metricDefinitionId The Metric Definition for which permissions will be deleted.
     * @param who The service/user id for which the permissions will be deleted.
     */
    public void deletePermission(String metricDefinitionId, String who){

        var accessControl = accessControlRepository.findByWhoAndCollectionAndEntity(who, Collection.MetricDefinition, metricDefinitionId);

        metricDefinitionRepository.deletePermission(accessControl);
    }

    /**
     * Fetches the Access Control that has been created for given metricDefinitionId and who
     *
     * @param metricDefinitionId The Metric Definition for which permissions will be deleted.
     * @param who The service/user id for which the permissions will be deleted.
     */
    public AccessControlResponseDto fetchPermission(String metricDefinitionId, String who){

        var accessControl = metricDefinitionRepository.getPermission(metricDefinitionId, who);

        return AccessControlMapper.INSTANCE.accessControlToResponse(accessControl);
    }

    /**
     * Fetches all Access Control that have been created for Metric Definition collection
     **/
    public List<AccessControlResponseDto> fetchAllPermissions(){

        var accessControl = metricDefinitionRepository.getAllPermissions();

        return AccessControlMapper.INSTANCE.accessControlsToResponse(accessControl);
    }
    public List<MetricDefinitionResponseDto> searchMetricDefinition( String json, boolean isAlwaysPermission) throws ParseException, NoSuchFieldException {

        List<String> entityIds=new ArrayList<>();
        if(!isAlwaysPermission){
            entityIds= fetchAllMetricDefinitions().stream().map(MetricDefinitionResponseDto::getId).collect(Collectors.toList());
        }
        Bson query=queryParser.parseFile(json, isAlwaysPermission, entityIds);
        return  MetricDefinitionMapper.INSTANCE.metricDefinitionsToResponse( metricDefinitionRepository.search(query));
    }
}