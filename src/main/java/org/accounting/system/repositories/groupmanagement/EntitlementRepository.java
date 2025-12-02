package org.accounting.system.repositories.groupmanagement;

import com.mongodb.client.model.Aggregates;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import org.accounting.system.entities.actor.Entitlement;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.repositories.modulators.AccessibleModulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EntitlementRepository extends AccessibleModulator<Entitlement, String> {

    public PanacheQuery<Entitlement> fetchAll(int page, int size) {

        var entitlements = getMongoCollection()
                .aggregate(List.of(Aggregates.skip(size * (page)), Aggregates.limit(size)), Entitlement.class)
                .into(new ArrayList<>());

        var count = getMongoCollection()
                .aggregate(List.of(Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<Entitlement>();

        projectionQuery.list = entitlements;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public Optional<Entitlement> findByName(String name){

        return find("name", name).stream().findAny();
    }
}
