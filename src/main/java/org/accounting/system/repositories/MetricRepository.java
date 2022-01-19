package org.accounting.system.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.entities.Metric;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This repository {@link MetricRepository} encapsulates the logic required to access
 * Metric data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the Metric collection. Finally, is is responsible for mapping
 * the data from the storage format to the {@link Metric}.
 */
@ApplicationScoped
public class MetricRepository implements PanacheMongoRepository<Metric> {

    public List<Metric> findMetricsByMetricDefinitionId(String metricDefinitionId){

        return find("metricDefinitionId = ?1", metricDefinitionId).stream().collect(Collectors.toList());
    }

    public long countMetricsByMetricDefinitionId(String metricDefinitionId){

        return count("metricDefinitionId = ?1", metricDefinitionId);
    }

}
