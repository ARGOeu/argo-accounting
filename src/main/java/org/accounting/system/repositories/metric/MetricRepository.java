package org.accounting.system.repositories.metric;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.vavr.collection.Array;
import jakarta.enterprise.context.ApplicationScoped;
import org.accounting.system.dtos.metric.UpdateMetricRequestDto;
import org.accounting.system.entities.Metric;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.modulators.AccessibleModulator;
import org.accounting.system.util.Utility;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link MetricRepository This repository} encapsulates the logic required to access
 * {@link Metric} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link Metric} collection. It is also responsible for mapping
 * the data from the storage format to the {@link Metric}.
 * <p>
 * Since {@link MetricRepository this repository} extends {@link AccessibleModulator},
 * it has access to all queries, which determine the degree of accessibility of the data.
 * <p>
 * Also, all the operations that are defined on {@link PanacheMongoRepository} are available on this repository.
 * In this repository, we essentially define the queries that will be executed on the database without any restrictions.
 */
@ApplicationScoped
public class MetricRepository extends AccessibleModulator<Metric, ObjectId> {

    public List<Metric> findMetricsByMetricDefinitionId(String metricDefinitionId) {

        return find("metricDefinitionId = ?1", metricDefinitionId).stream().collect(Collectors.toList());
    }

    /**
     * Executes a query to count the Metrics assigned to a Metric Definition.
     *
     * @param metricDefinitionId
     * @return the number of the Metrics assigned to the given metric definition id
     */
    public long countMetricsByMetricDefinitionId(String metricDefinitionId) {

        return count("metricDefinitionId = ?1", metricDefinitionId);
    }

    /**
     * Executes a query to find a Metric by given id.
     *
     * @param id The metric id
     * @return Optional of Metric
     */
    public Optional<Metric> findMetricById(String id) {

        return findByIdOptional(new ObjectId(id));
    }

    public MongoQuery<MetricProjection> searchMetrics(Bson searchDoc,List<String> installationsIds,int page, int size) {
        var installations=Aggregates.match(Filters.in("resource_id", installationsIds));

        var addField = new Document("$addFields", new Document("metric_definition_id", new Document("$toObjectId", "$metric_definition_id")));

        Bson lookup = Aggregates.lookup("MetricDefinition", "metric_definition_id", "_id", "metric_definition");

        var metrics= getMongoCollection()
                .aggregate(List.of(installations,Aggregates.match(searchDoc), addField, Aggregates.skip(size * (page)), Aggregates.limit(size), lookup), MetricProjection.class)
                .into(new ArrayList<>());
        Document count = getMongoCollection()
                .aggregate(List.of(installations,Aggregates.match(searchDoc), addField, Aggregates.count()))
                .first();
        var projectionQuery = new MongoQuery<MetricProjection>();

        projectionQuery.list = metrics;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public PanacheQuery<MetricProjection> findByExternalId(final String externalId, int page, int size, String start, String end, String metricDefinitionId) {

        var filters = Array.of(Filters.regex("resource_id","^"+externalId+"(?:\\.[^\\r\\n.]+)*$"));

        var addField = new Document("$addFields", new Document("metric_definition_id", new Document("$toObjectId", "$metric_definition_id")));

        Bson lookup = Aggregates.lookup("MetricDefinition", "metric_definition_id", "_id", "metric_definition");

        if(ObjectUtils.allNotNull(start, end)) {

            if(Utility.isDate(start, end) && Utility.isBefore(start, end)){


                filters = filters.append(Filters.and(Filters.gte("time_period_start", Utility.stringToInstant(start)), Filters.lte("time_period_start", Utility.stringToInstant(end))));
                filters = filters.append(Filters.and(Filters.gte("time_period_end", Utility.stringToInstant(start)), Filters.lte("time_period_end", Utility.stringToInstant(end))));
            }

        } else if(ObjectUtils.isNotEmpty(start)){

            if(Utility.isDate(start)){


                filters = filters.append(Filters.and(Filters.gte("time_period_start", Utility.startToInstant(start))));
            }
        } else if(ObjectUtils.isNotEmpty(end)){

            filters = filters.append(Filters.and(Filters.lte("time_period_end", Utility.endToInstant(end))));
        }

        if(StringUtils.isNotEmpty(metricDefinitionId)){

            filters = filters.append(Filters.eq("metric_definition_id", metricDefinitionId));
        }

        Bson regex = Aggregates.match(Filters.and(filters));

        List<MetricProjection> projections = getMongoCollection()
                .aggregate(List
                        .of(regex, addField, Aggregates.skip(size * (page)), Aggregates.limit(size), lookup), MetricProjection.class).into(new ArrayList<>());

        Document count = getMongoCollection()
                .aggregate(List
                        .of(regex, addField, Aggregates.count())).first();

        var projectionQuery = new MongoQuery<MetricProjection>();

        projectionQuery.list = projections;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public PanacheQuery<MetricProjection> findByProjectIdAndGroupId(final String externalId, final String groupId, int page, int size, String start, String end) {

        var filters = Array.of(Filters.regex("resource_id", "^"+externalId+"(?:\\.[^\\r\\n.]+)*$"));

        var addField = new Document("$addFields", new Document("metric_definition_id", new Document("$toObjectId", "$metric_definition_id")));

        var lookup = Aggregates.lookup("MetricDefinition", "metric_definition_id", "_id", "metric_definition");

        filters = filters.append(Filters.eq("group_id", groupId));

        if(ObjectUtils.allNotNull(start, end)) {

            if(Utility.isDate(start, end) && Utility.isBefore(start, end)){


                filters = filters.append(Filters.and(Filters.gte("time_period_start", Utility.stringToInstant(start)), Filters.lte("time_period_start", Utility.stringToInstant(end))));
                filters = filters.append(Filters.and(Filters.gte("time_period_end", Utility.stringToInstant(start)), Filters.lte("time_period_end", Utility.stringToInstant(end))));
            }

        } else if(ObjectUtils.isNotEmpty(start)){

            if(Utility.isDate(start)){


                filters = filters.append(Filters.and(Filters.gte("time_period_start", Utility.startToInstant(start))));
            }
        } else if(ObjectUtils.isNotEmpty(end)){

               filters = filters.append(Filters.and(Filters.lte("time_period_end", Utility.endToInstant(end))));
        }

        var regex = Aggregates.match(Filters.and(filters));

        var projections = getMongoCollection()
                .aggregate(List
                        .of(regex, addField, Aggregates.skip(size * (page)), Aggregates.limit(size), lookup), MetricProjection.class).into(new ArrayList<>());

       var count = getMongoCollection()
                .aggregate(List
                        .of(regex, addField, Aggregates.count())).first();

        var projectionQuery = new MongoQuery<MetricProjection>();

        projectionQuery.list = projections;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public PanacheQuery<MetricProjection> findByProjectIdAndUserId(final String externalId, final String userId, int page, int size, String start, String end) {

        var filters = Array.of(Filters.regex("resource_id", "^"+externalId+"(?:\\.[^\\r\\n.]+)*$"));

        var addField = new Document("$addFields", new Document("metric_definition_id", new Document("$toObjectId", "$metric_definition_id")));

        Bson lookup = Aggregates.lookup("MetricDefinition", "metric_definition_id", "_id", "metric_definition");

        filters = filters.append(Filters.eq("user_id", userId));

        if(ObjectUtils.allNotNull(start, end)) {

            if(Utility.isDate(start, end) && Utility.isBefore(start, end)){


                filters = filters.append(Filters.and(Filters.gte("time_period_start", Utility.stringToInstant(start)), Filters.lte("time_period_start", Utility.stringToInstant(end))));
                filters = filters.append(Filters.and(Filters.gte("time_period_end", Utility.stringToInstant(start)), Filters.lte("time_period_end", Utility.stringToInstant(end))));
            }

        } else if(ObjectUtils.isNotEmpty(start)){

            if(Utility.isDate(start)){


                filters = filters.append(Filters.and(Filters.gte("time_period_start", Utility.startToInstant(start))));
            }
        } else if(ObjectUtils.isNotEmpty(end)){

            filters = filters.append(Filters.and(Filters.lte("time_period_end", Utility.endToInstant(end))));
        }

        Bson regex = Aggregates.match(Filters.and(filters));

        List<MetricProjection> projections = getMongoCollection()
                .aggregate(List
                        .of(regex, addField, Aggregates.skip(size * (page)), Aggregates.limit(size), lookup), MetricProjection.class).into(new ArrayList<>());

        Document count = getMongoCollection()
                .aggregate(List
                        .of(regex, addField, Aggregates.count())).first();

        var projectionQuery = new MongoQuery<MetricProjection>();

        projectionQuery.list = projections;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public Optional<Metric> findFirstByInstallationId(String installationId){
        return find("installation_id", installationId).firstResultOptional();
    }

    /**
     * This method is responsible for updating a part or all attributes of existing Metric.
     *
     * @param id The Metric to be updated.
     * @param request The Metric attributes to be updated.
     * @return The updated Metric.
     */
    public Metric updateEntity(ObjectId id, UpdateMetricRequestDto request) {

        Metric entity = findById(id);

        MetricMapper.INSTANCE.updateMetricFromDto(request, entity);

        super.update(entity);

        return entity;
    }

    public void deleteByProjectId(String projectId) {
        delete("project_id", projectId);
    }
}
