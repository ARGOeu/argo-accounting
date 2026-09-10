package org.grnet.creditmanagement.repositories;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.grnet.creditmanagement.entities.RatingPolicyEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RatingPolicyRepository implements PanacheMongoRepositoryBase<RatingPolicyEntity, String> {

    public boolean existsByInstallationMetricAndValidFrom(String installationId, String metricDefinitionId, Instant validFrom) {

        return find("installationId = ?1 and metricDefinitionId = ?2 and validFrom = ?3",
                installationId, metricDefinitionId, validFrom)
                .firstResultOptional()
                .isPresent();
    }

    /**
     * Returns the earliest valid_from currently recorded for this
     * installation + metric definition combination, if any entries exist.
     */
    public Optional<Instant> findEarliestValidFrom(String installationId, String metricDefinitionId) {

        return find("installationId = ?1 and metricDefinitionId = ?2", Sort.by("validFrom"),
                installationId, metricDefinitionId)
                .firstResultOptional()
                .map(RatingPolicyEntity::getValidFrom);
    }

    /**
     * Returns, for the given installation, one RatingPolicyEntity per
     * metric_definition_id: the entry with the latest valid_from that is
     * less than or equal to the given point in time. Metric definitions
     * with no entry at or before that time are simply absent from the
     * result.
     *
     * This is a raw aggregation pipeline (bypasses PanacheQL), so field
     * names here MUST match the actual BSON field names as declared via
     * @BsonProperty on the entity (snake_case), not the Java field names.
     */
    public List<RatingPolicyEntity> findCurrentEffectiveRates(String installationId, Instant asOf) {

        var match = Aggregates.match(Filters.and(
                Filters.eq("installation_id", installationId),
                Filters.lte("valid_from", asOf)
        ));

        var sort = Aggregates.sort(Sorts.descending("valid_from"));

        var group = Aggregates.group("$metric_definition_id", Accumulators.first("doc", "$$ROOT"));

        var replaceRoot = Aggregates.replaceRoot("$doc");

        return mongoCollection()
                .aggregate(List.of(match, sort, group, replaceRoot), RatingPolicyEntity.class)
                .into(new ArrayList<>());
    }

    /**
     * Returns a page of all RatingPolicy entries for the given installation,
     * across all metric definitions, ordered by metric_definition_id then
     * valid_from ascending. page is 1-based.
     */
    public PanacheQuery<RatingPolicyEntity> findByInstallation(String installationId, int page, int size) {

        return find("installationId = ?1", Sort.by("metricDefinitionId").and("validFrom"), installationId)
                .page(page - 1, size);
    }

    /**
     * All Rating Policy entries for the given installation and metric
     * definition, ordered by valid_from ascending — including entries
     * outside any particular reporting window, since the earliest ones
     * are needed to determine which rate was active at the start of a
     * window.
     */
    public List<RatingPolicyEntity> findAllOrderedByValidFrom(String installationId, String metricDefinitionId) {

        return find("installationId = ?1 and metricDefinitionId = ?2", Sort.by("validFrom"),
                installationId, metricDefinitionId)
                .list();
    }

    /**
     * Distinct metric_definition_ids that have at least one Rating Policy
     * entry on the given installation, optionally restricted to a single
     * metric_definition_id.
     */
    public List<String> findDistinctMetricDefinitionIds(String installationId, String metricDefinitionIdFilter) {

        var filters = new ArrayList<org.bson.conversions.Bson>();
        filters.add(Filters.eq("installation_id", installationId));

        if (metricDefinitionIdFilter != null) {
            filters.add(Filters.eq("metric_definition_id", metricDefinitionIdFilter));
        }

        return mongoCollection()
                .distinct("metric_definition_id", Filters.and(filters), String.class)
                .into(new ArrayList<>());
    }
}
