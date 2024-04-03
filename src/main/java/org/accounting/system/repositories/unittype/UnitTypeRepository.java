package org.accounting.system.repositories.unittype;

import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import org.accounting.system.dtos.unittype.UpdateUnitTypeRequestDto;
import org.accounting.system.entities.UnitType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.UnitTypeMapper;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.modulators.AccessibleModulator;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import java.util.Optional;

/**
 * {@link UnitTypeRepository This repository} encapsulates the logic required to access
 * {@link UnitType} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link UnitType} collection. It is also responsible for mapping
 * the data from the storage format to the {@link UnitType}.
 *
 * Since {@link UnitTypeRepository this repository} extends {@link AccessibleModulator},
 * it has access to all queries, which determine the degree of accessibility of the data.
 *
 * Also, all the operations that are defined on {@link PanacheMongoRepository} are available on this repository.
 * In this repository, we essentially define the queries that will be executed on the database without any restrictions.
 */
@ApplicationScoped
public class UnitTypeRepository extends AccessibleModulator<UnitType, ObjectId> {

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    /**
     * This method fetches an entity from the database by given unit type.
     *
     * @param unitType The Unit Type that has been requested.
     * @return The wrapped Unit Type as Optional.
     */
    public Optional<UnitType> fetchUnitByType(String unitType){

        return find("unit = ?1", unitType)
                .withCollation(Collation.builder().locale("en")
                        .collationStrength(CollationStrength.SECONDARY).build())
                .stream().findAny();
    }

    /**
     * This method is responsible for updating a part or all attributes of an existing Unit Type.
     *
     * @param id The Unit Type to be updated.
     * @param request The Unit Type attributes that have been requested to be updated.
     * @throws ForbiddenException If the editing action is not allowed.
     * @return The updated Unit Type.
     */
    public UnitType updateEntity(ObjectId id, UpdateUnitTypeRequestDto request) {

        UnitType entity = findById(id);

        if(StringUtils.isEmpty(entity.getCreatorId())){
            throw new ForbiddenException("You cannot update a Unit Type registered by Accounting Service.");
        }

        var isUsed = metricDefinitionRepository.unitTypeUsedInMetricDefinition(entity.getUnit());

        if(isUsed){
            throw new ForbiddenException("This Unit Type is used in an existing Metric Definition, so you cannot update it.");
        }

        if(StringUtils.isNotEmpty(request.unit)){
            var optional = fetchUnitByType(request.unit);

            optional.ifPresent(ut -> {throw new ConflictException("This Unit Type already exists in the Accounting Service database. Its id is "+ut.getId().toString());});
        }

        UnitTypeMapper.INSTANCE.updateUnitTypeFromDto(request, entity);

        return super.updateEntity(entity, id);
    }
}