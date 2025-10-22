package org.accounting.system.repositories.groupmanagement;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import org.accounting.system.entities.actor.ActorEntitlements;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.repositories.modulators.AccessibleModulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ActorEntitlementsRepository extends AccessibleModulator<ActorEntitlements, String> {


    public boolean findAnyEntitlement(String entitlementId){

        return find("entitlementId", entitlementId).stream().findAny().isPresent();
    }

    public List<String> findActorEntitlements(String actorId){

        var eq = Aggregates
                .match(Filters.eq("actor_id", actorId));

        var lookup = Aggregates.lookup("Entitlement", "entitlement_id", "_id", "details");

        var unwindDetails = Aggregates
                .unwind("$details");

        var projection = Aggregates.project(Projections.fields(
                Projections.excludeId(),
                Projections.computed("name", "$details.name")));

        var names = getMongoCollection()
                .aggregate(List.of(eq, lookup, unwindDetails, projection))
                        .into(new ArrayList<>());

        return names.stream().map(doc->doc.getString("name")).collect(Collectors.toList());
    }

    public PanacheQuery<ActorEntitlements> fetchAll(int page, int size) {

        var clients = getMongoCollection()
                .aggregate(List.of(Aggregates.skip(size * (page)), Aggregates.limit(size)), ActorEntitlements.class)
                .into(new ArrayList<>());

        var count = getMongoCollection()
                .aggregate(List.of(Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<ActorEntitlements>();

        projectionQuery.list = clients;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public Optional<ActorEntitlements> findByEntitlementAndActor(String entitlementId, String actorId){

        return find("actorId = ?1 and entitlementId = ?2", actorId, entitlementId).firstResultOptional();
    }
}
