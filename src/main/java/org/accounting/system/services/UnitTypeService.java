package org.accounting.system.services;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.unittype.UnitTypeDto;
import org.accounting.system.dtos.unittype.UpdateUnitTypeRequestDto;
import org.accounting.system.endpoints.UnitTypeEndpoint;
import org.accounting.system.entities.UnitType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.UnitTypeMapper;
import org.accounting.system.repositories.unittype.UnitTypeRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import java.util.Optional;

/**
 * This service exposes business logic, which uses the {@link UnitTypeEndpoint}.
 * It is used to keep logic to a minimum in {@link UnitTypeEndpoint} and
 * {@link UnitTypeRepository}
 */
@ApplicationScoped
public class UnitTypeService {

    @Inject
    UnitTypeRepository unitTypeRepository;

    @Inject
    MetricDefinitionService metricDefinitionService;

    /**
     * Maps the {@link UnitTypeDto incoming request} to {@link UnitType database entity}.
     * Then the transformed {@link UnitType entity} is stored in the mongo database.
     *
     * @param request The POST request body.
     * @return The stored Unit Type has been turned into a response body.
     */
    public UnitTypeDto save(UnitTypeDto request) {

        var optional = getUnitByType(request.unit);

        optional.ifPresent(ut -> {throw new ConflictException("This Unit Type already exists in the Accounting Service database. Its id is "+ut.getId().toString());});

        var unitType = UnitTypeMapper.INSTANCE.requestToUnitType(request);

        unitTypeRepository.save(unitType);

        return UnitTypeMapper.INSTANCE.unitTypeToResponse(unitType);
    }

    /**
     * Deletes an existing Unit Type by given id.
     * @param id The Unit Type to be deleted.
     * @return If the operation is successful or not.
     * @throws ForbiddenException If the given Unit Type is used in a Metric Definition.
     */
    public boolean delete(String id){

        var unitType = unitTypeRepository.findById(new ObjectId(id));

        if(StringUtils.isEmpty(unitType.getCreatorId())){
            throw new ForbiddenException("You cannot delete a Unit Type registered by Accounting Service.");
        }

        var isUsed = metricDefinitionService.unitTypeUsedInMetricDefinition(unitType.getUnit());

        if(isUsed){
            throw new ForbiddenException("This Unit Type is used in an existing Metric Definition, so you cannot delete it.");
        }

        return unitTypeRepository.deleteEntityById(new ObjectId(id));
    }

    /**
     * This method returns a specific entity by given type. If the Unit Type does not exist, the Optional value is empty.
     *
     * @param unitType The Unit Type to be retrieved.
     * @return The wrapped Unit Type as Optional.
     */
    public Optional<UnitType> getUnitByType(String unitType){

       return unitTypeRepository.fetchUnitByType(unitType);
    }

    /**
     * This method fetches a specific entity by given id and turns it into a response body.
     *
     * @param id The Unit Type ID to be retrieved.
     * @return The wrapped Unit Type as Optional.
     */
    public UnitTypeDto getUnitTypeById(String id){

        var entity = unitTypeRepository.findById(new ObjectId(id));
        return UnitTypeMapper.INSTANCE.unitTypeToResponse(entity);
    }

    /**
     * Returns the N Unit Types from the given page.
     *
     * @param page Indicates the page number.
     * @param size The number of Unit Types to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<UnitTypeDto> findAllUnitTypesPageable(int page, int size, UriInfo uriInfo){

        PanacheQuery<UnitType> panacheQuery = unitTypeRepository.findAllPageable(page, size);

        return new PageResource<>(panacheQuery, UnitTypeMapper.INSTANCE.unitTypesToResponse(panacheQuery.list()), uriInfo);
    }

    /**
     * This method calls the {@link UnitTypeRepository unitTypeRepository} to update an existing Unit Type.
     *
     * @param id The Unit Type to be updated.
     * @param request The Unit Type attributes that have been requested to be updated.
     * @return The updated Unit Type.
     * @throws ConflictException If the Unit Type to be updated exists
     */
    public UnitTypeDto update(String id, UpdateUnitTypeRequestDto request){

        UnitType unitType = null;


        unitType = unitTypeRepository.updateEntity(new ObjectId(id), request);

        return UnitTypeMapper.INSTANCE.unitTypeToResponse(unitType);
    }
}
