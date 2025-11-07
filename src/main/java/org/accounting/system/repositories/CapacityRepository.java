package org.accounting.system.repositories;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import org.accounting.system.entities.Capacity;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.repositories.modulators.AccessibleModulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CapacityRepository extends AccessibleModulator<Capacity, String> {

    public Optional<Capacity> findByInstallationAndMetricDefinition(String installationId, String metricDefinitionId){

        return find("installationId = ?1 and metricDefinitionId = ?2", installationId, metricDefinitionId).firstResultOptional();
    }

    public PanacheQuery<Capacity> findInstallationCapacities(String installationId, int page, int size){

        var eq = Aggregates
                .match(Filters.eq("installation_id", installationId));

        var capacities = getMongoCollection()
                .aggregate(List.of(eq, Aggregates.skip(size * (page)), Aggregates.limit(size)), Capacity.class)
                .into(new ArrayList<>());

        var count = getMongoCollection()
                .aggregate(List.of(eq, Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<Capacity>();

        projectionQuery.list = capacities;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }
}
