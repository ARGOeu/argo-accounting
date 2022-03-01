package org.accounting.system.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.dtos.UpdateMetricDefinitionRequestDto;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.repositories.authorization.AccessEntityRepository;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

/**
 * This repository {@link MetricDefinitionRepository} encapsulates the logic required to access
 * Metric Definition data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the MetricDefinition collection. Finally, is is responsible for mapping
 * the data from the storage format to the {@link MetricDefinition}. To adjust the degree of accessibility to queries,
 * {@link AccessEntityRepository} public method must be executed, not the {@link PanacheMongoRepository} default methods.
 */
@ApplicationScoped
public class MetricDefinitionRepository extends AccessEntityRepository<MetricDefinition> implements PanacheMongoRepository<MetricDefinition> {

    public Optional<MetricDefinition> exist(String unitType, String name){

        return find("unitType = ?1 and metricName = ?2", unitType, name.toLowerCase()).stream().findAny();
    }

    @Override
    protected MetricDefinition getEntityById(ObjectId objectId) {

        return findById(objectId);
    }

    @Override
    protected <U> MetricDefinition updateEntity(ObjectId id, U updateDto, MetricDefinition entity) {

        MetricDefinitionMapper.INSTANCE.updateMetricDefinitionFromDto((UpdateMetricDefinitionRequestDto) updateDto, entity);
        update(entity);
        return entity;
    }

    @Override
    protected boolean deleteEntity(ObjectId id) {
        return deleteById(id);
    }

    @Override
    protected List<MetricDefinition> getAll() {
        return findAll().list();
    }

    @Override
    protected List<MetricDefinition> getAllByCreatorId(String creatorId) {
        return list("creatorId = ?1", creatorId);
    }
}
