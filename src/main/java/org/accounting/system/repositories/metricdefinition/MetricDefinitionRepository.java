package org.accounting.system.repositories.metricdefinition;

import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.dtos.metricdefinition.UpdateMetricDefinitionRequestDto;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.repositories.modulators.AccessibleModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;

/**
 * {@link MetricDefinitionRepository This repository} encapsulates the logic required to access
 * {@link MetricDefinition} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link MetricDefinition} collection. It is also responsible for mapping
 * the data from the storage format to the {@link MetricDefinition}.
 *
 * Since {@link MetricDefinitionRepository this repository} extends {@link AccessibleModulator},
 * it has access to all queries, which determine the degree of accessibility of the data.
 *
 * Also, all the operations that are defined on {@link PanacheMongoRepository} are available on this repository.
 * In this repository, we essentially define the queries that will be executed on the database without any restrictions.
 */
@ApplicationScoped
public class MetricDefinitionRepository extends AccessibleModulator<MetricDefinition, ObjectId> {


    /**
     * This method checks if a particular Metric Definition exists in the database.
     *
     * @param unitType The Metric Definition Unit Type.
     * @param name The Metric Definition name.
     * @throws ConflictException If Metric Definition already exists.
     */
    public void exist(String unitType, String name){

        var optional = find("unitType = ?1 and metricName = ?2", unitType, name)
                .withCollation(Collation.builder().locale("en")
                        .collationStrength(CollationStrength.SECONDARY).build())
                .stream().findAny();

        optional.ifPresent(metricDefinition -> {throw new ConflictException("There is a Metric Definition with unit type "+unitType+" and name "+name+". Its id is "+metricDefinition.getId().toString());});
    }

    /**
     * This method is responsible for updating a part or all attributes of an existing Metric Definition.
     *
     * @param id The Metric Definition to be updated.
     * @param request The Metric Definition attributes to be updated.
     * @return The updated Metric Definition.
     */
    public MetricDefinition updateEntity(ObjectId id, UpdateMetricDefinitionRequestDto request) {

        MetricDefinition entity = findById(id);

        MetricDefinitionMapper.INSTANCE.updateMetricDefinitionFromDto(request, entity);

        return super.updateEntity(entity, id);
    }

    /**
     * This method execute a query to database to check if a Unit Type is used in an existing Metric Definition.
     * @param unitType The Unit Type to be checked.
     * @return Whether the given Unit Type is used in any Metric Definition.
     */
    public boolean unitTypeUsedInMetricDefinition(String unitType){

        return find("unitType = ?1", unitType)
                .withCollation(Collation.builder().locale("en")
                        .collationStrength(CollationStrength.SECONDARY).build())
                .stream().findAny().isPresent();
    }
}