package org.accounting.system.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import org.accounting.system.entities.Metric;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
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

    /**
     * Executes a query to count the Metrics assigned to a Metric Definition.
     *
     * @param metricDefinitionId
     * @return the number of the Metrics assigned to the given metric definition id
     */
    public long countMetricsByMetricDefinitionId(String metricDefinitionId){

        return count("metricDefinitionId = ?1", metricDefinitionId);
    }

    /**
     *  Executes a query to find a Metric by given id.
     *
     * @param id The metric id
     * @return Optional of Metric
     */
    public Optional<Metric> findMetricById(String id){

        return findByIdOptional(new ObjectId(id));
    }

    /**
     * Executes a query in mongo database and returns the paginated results ordered by metricDefinitionId.
     * The page parameter indicates the requested page number, and the size parameter the number of entities by page.
     *
     * @param metricDefinitionId The Metric Definition id
     * @param page The page to be retrieved
     * @param size The requested size of page
     * @return An object represents the paginated results
     */
    public PanacheQuery<Metric> findMetricsByMetricDefinitionIdPageable(String metricDefinitionId, int page, int size){

        return find("metricDefinitionId", Sort.by("metricDefinitionId"), metricDefinitionId).page(Page.of(page, size));
    }
}
