package org.accounting.system.repositories.metricdefinition;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.entities.MetricDefinition;

import javax.enterprise.context.ApplicationScoped;

/**
 * {@link MetricDefinitionRepository This repository} encapsulates the logic required to access
 * {@link MetricDefinition} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link MetricDefinition} collection. It is also responsible for mapping
 * the data from the storage format to the {@link MetricDefinition}.
 *
 * Since {@link MetricDefinitionRepository this repository} extends {@link MetricDefinitionModulator},
 * it has access to all queries, which determine the degree of accessibility of the data.
 *
 * Also, all the operations that are defined on {@link PanacheMongoRepository} are available on this repository.
 * In this repository, we essentially define the queries that will be executed on the database without any restrictions.
 */
@ApplicationScoped
public class MetricDefinitionRepository extends MetricDefinitionModulator {


}