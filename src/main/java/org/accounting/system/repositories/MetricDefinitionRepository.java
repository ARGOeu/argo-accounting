package org.accounting.system.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.entities.MetricDefinition;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

/**
 * This repository {@link MetricDefinitionRepository} encapsulates the logic required to access
 * Metric Definition data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the MetricDefinition collection. Finally, is is responsible for mapping
 * the data from the storage format to the {@link MetricDefinition}.
 */
@ApplicationScoped
public class MetricDefinitionRepository implements PanacheMongoRepository<MetricDefinition> {

    public Optional<MetricDefinition> exist(String unitType, String name){

        return find("unitType = ?1 and metricName = ?2", unitType.toLowerCase(), name.toLowerCase()).stream().findAny();
    }
}
