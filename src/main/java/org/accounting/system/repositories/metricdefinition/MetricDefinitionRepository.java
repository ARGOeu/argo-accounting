package org.accounting.system.repositories.metricdefinition;

import org.accounting.system.dtos.UpdateMetricDefinitionRequestDto;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.repositories.modulators.AbstractModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

/**
 * This repository {@link MetricDefinitionRepository} encapsulates the logic required to access
 * {@link MetricDefinition} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link MetricDefinition} collection. It is also responsible for mapping
 * the data from the storage format to the {@link MetricDefinition}.
 *
 * Since {@link MetricDefinitionRepository} extends {@link AbstractModulator},
 * it has to provide it with the corresponding {@link org.accounting.system.repositories.modulators.AccessModulator} implementations.
 * Also, all the operations that are defined on {@link io.quarkus.mongodb.panache.PanacheMongoRepository} and on
 * {@link org.accounting.system.repositories.modulators.AccessModulator} are available on this repository.
 */
@ApplicationScoped
public class MetricDefinitionRepository extends MetricDefinitionModulator {

    public Optional<MetricDefinition> exist(String unitType, String name){

        return find("unitType = ?1 and metricName = ?2", unitType, name.toLowerCase()).stream().findAny();
    }

    public MetricDefinition updateEntity(ObjectId id, UpdateMetricDefinitionRequestDto updateMetricDefinitionRequestDto) {

        MetricDefinition entity = findById(id);

        MetricDefinitionMapper.INSTANCE.updateMetricDefinitionFromDto(updateMetricDefinitionRequestDto, entity);

        return super.updateEntity(entity);
    }
}