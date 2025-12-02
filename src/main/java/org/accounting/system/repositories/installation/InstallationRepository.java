package org.accounting.system.repositories.installation;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UnwindOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.vavr.collection.Array;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.entities.Capacity;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.CapacityPeriod;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.InstallationReport;
import org.accounting.system.entities.projections.MetricGroupResults;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.enums.RelationType;
import org.accounting.system.repositories.CapacityRepository;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.metricdefinition.MetricDefinitionRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * {@link InstallationRepository This repository} encapsulates the logic required to access
 * {@link Installation} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link Installation} collection. It is also responsible for mapping
 * the data from the storage format to the {@link Installation}.
 */
@ApplicationScoped
public class InstallationRepository {

    @Inject
    ProjectRepository projectRepository;

    @Inject
    MetricRepository metricRepository;

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    @Inject
    CapacityRepository capacityRepository;

    @Inject
    MetricDefinitionRepository metricDefinitionRepository;

    public Optional<Installation> exist(String projectID, String providerID, String installationID){

        var project = Aggregates
                .match(Filters.eq("_id", projectID));

        var unwind = Aggregates.unwind("$providers");

        var provider = Aggregates
                .match(Filters.eq("providers._id", providerID));

        var replaceRoot = Aggregates
                .replaceRoot("$providers");

        var unwindInstallations = Aggregates.unwind("$installations");

        var eqInstallation = Aggregates
                .match(Filters.eq("installations.installation", installationID));

        var replaceRootToInstallation = Aggregates.replaceRoot("$installations");

        return Optional.ofNullable(projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindInstallations, eqInstallation, unwindInstallations, replaceRootToInstallation), Installation.class)
                .first());
    }

    /**
     * Retrieves from database an Installation joined with Metric Definition.
     *
     * @param projectID The Project id.
     * @param providerID The Provider id.
     * @param installationID The Installation id.
     * @return The corresponding Installation joined with Metric Definition.
     */
    public InstallationProjection fetchInstallationProjection(String projectID, String providerID, String installationID){

        var project = Aggregates
                .match(Filters.eq("_id", projectID));

        var unwind = Aggregates.unwind("$providers");

        var provider = Aggregates
                .match(Filters.eq("providers._id", providerID));

        var replaceRoot = Aggregates
                .replaceRoot("$providers");

        var unwindInstallations = Aggregates.unwind("$installations");

        var eqInstallation = Aggregates
                .match(Filters.eq("installations._id", installationID));

        var replaceRootToInstallation = Aggregates.replaceRoot("$installations");

        var lookup = Aggregates.lookup("MetricDefinition", "unit_of_access", "_id", "unit_of_access");

        var lookupCapacity = Aggregates.lookup("Capacity", "_id", "installation_id", "capacities");

        var lookupCapacitiesMetricDefinitions = Aggregates.lookup("MetricDefinition", "metric_definition_id", "_id", "metric_definition");

        return projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindInstallations, eqInstallation, unwindInstallations, replaceRootToInstallation, lookup, lookupCapacity, lookupCapacitiesMetricDefinitions), InstallationProjection.class)
                .first();
    }

    /**
     * Retrieves from database a stored Installation.
     *
     * @param projectID The Project id.
     * @param providerID The Provider id.
     * @param installationID The Installation id.
     * @return The corresponding Installation.
     */
    public Installation fetchInstallation(String projectID, String providerID, String installationID){

        var project = Aggregates
                .match(Filters.eq("_id", projectID));

        var unwind = Aggregates.unwind("$providers");

        var provider = Aggregates
                .match(Filters.eq("providers._id", providerID));

        var replaceRoot = Aggregates
                .replaceRoot("$providers");

        var unwindInstallations = Aggregates.unwind("$installations");

        var eqInstallation = Aggregates
                .match(Filters.eq("installations._id", installationID));

        var replaceRootToInstallation = Aggregates.replaceRoot("$installations");

        return projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindInstallations, eqInstallation, unwindInstallations, replaceRootToInstallation), Installation.class)
                .first();
    }

    public Optional<Installation> fetchInstallationByExternalId(String projectID, String providerID, String externalInstallationId){

        var project = Aggregates
                .match(Filters.eq("_id", projectID));

        var unwind = Aggregates.unwind("$providers");

        var provider = Aggregates
                .match(Filters.eq("providers._id", providerID));

        var replaceRoot = Aggregates
                .replaceRoot("$providers");

        var unwindInstallations = Aggregates.unwind("$installations");

        var eqInstallation = Aggregates
                .match(Filters.eq("installations.external_id", externalInstallationId));

        var replaceRootToInstallation = Aggregates.replaceRoot("$installations");

        return Optional.ofNullable(projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindInstallations, eqInstallation, unwindInstallations, replaceRootToInstallation), Installation.class)
                .first());
    }

    public List<Installation> fetchInstallationProviders(String projectID, String providerID){

        var project = Aggregates
                .match(Filters.eq("_id", projectID));

        var unwind = Aggregates.unwind("$providers");

        var provider = Aggregates
                .match(Filters.eq("providers._id", providerID));

        var replaceRoot = Aggregates
                .replaceRoot("$providers");

        var unwindInstallations = Aggregates.unwind("$installations");

        var replaceRootToInstallation = Aggregates.replaceRoot("$installations");

        return projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindInstallations, replaceRootToInstallation), Installation.class)
                .into(new ArrayList<>());
    }

    public void deleteInstallation(String projectID, String providerID, String installationID){

        var query = Filters.and(Filters.eq("_id", projectID), Filters.eq("providers._id", providerID));

        projectRepository.getMongoCollection().updateOne(query, Updates.pull("providers.$.installations", new Document("_id", installationID)));
    }

    public void updateInstallation(String projectID, String providerID, String installationID, Installation installation){

        var update = Updates.set("providers.$[provider].installations.$[installation]", installation);

        var options =new UpdateOptions();

        options.arrayFilters(List.of(Filters.eq("provider._id", providerID), Filters.eq("installation._id", installationID)));

        var eq = Filters.and(Filters.eq("_id",  projectID));

        projectRepository.getMongoCollection().updateOne(eq, update, options);
    }

    public Installation save(InstallationRequestDto request, String creatorId) {

        var installation = insertNewInstallation(request, creatorId);

        HierarchicalRelation project = new HierarchicalRelation(request.project, RelationType.PROJECT);

        HierarchicalRelation provider = new HierarchicalRelation(request.organisation, project, RelationType.PROVIDER, request.organisation);

        HierarchicalRelation hinstallation = new HierarchicalRelation(installation.getId(), provider, RelationType.INSTALLATION, request.externalId);

        hierarchicalRelationRepository.save(project);
        hierarchicalRelationRepository.save(provider);
        hierarchicalRelationRepository.save(hinstallation);

        return installation;
    }

    public Installation insertNewInstallation(InstallationRequestDto request, String creatorId){

        var installation = new Installation();
        installation.setProject(request.project);
        installation.setOrganisation(request.organisation);
        installation.setInfrastructure(request.infrastructure);
        installation.setInstallation(request.installation);
        installation.setResource(request.resource);
        installation.setExternalId(request.externalId);
        installation.setUnitOfAccess(StringUtils.isNotEmpty(request.unitOfAccess) ? new ObjectId(request.unitOfAccess) : null);
        installation.setCreatorId(creatorId);
        installation.setId(new ObjectId().toString());

        var update = Updates.push("providers.$.installations", installation);

        var eq = Filters.and(Filters.eq("_id",  installation.getProject()), Filters.eq("providers._id",  installation.getOrganisation()));

        projectRepository.getMongoCollection().updateOne(eq, update);

        return installation;
    }

    public PanacheQuery<InstallationProjection> fetchAllInstallations(int page, int size) {

        var unwind = Aggregates
                .unwind("$providers");

        var replaceRoot = Aggregates
                .replaceRoot("$providers");
        var unwindOptions = new UnwindOptions();
        var unwindInstallations = Aggregates.unwind("$providers.installations",unwindOptions.preserveNullAndEmptyArrays(Boolean.FALSE));

        var replaceRootToInstallation = Aggregates.replaceRoot("$installations");

        var lookup = Aggregates.lookup("MetricDefinition", "unit_of_access", "_id", "unit_of_access");

        var lookupCapacity = Aggregates.lookup("Capacity", "_id", "installation_id", "capacities");

        var installations= projectRepository.getMongoCollection()
                .aggregate(List.of(unwind,unwindInstallations,replaceRoot,replaceRootToInstallation, Aggregates.skip(size * (page)), Aggregates.limit(size), lookup, lookupCapacity), InstallationProjection.class)
                .into(new ArrayList<>());

        var count = projectRepository.getMongoCollection()
                .aggregate(List.of(unwind,unwindInstallations,replaceRoot,replaceRootToInstallation,Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<InstallationProjection>();

        projectionQuery.list = installations;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public List<String> fetchAllInstallationIds() {

        var unwind = Aggregates
                .unwind("$providers");

        var replaceRoot = Aggregates
                .replaceRoot("$providers");
        var unwindOptions = new UnwindOptions();
        var unwindInstallations = Aggregates.unwind("$providers.installations",unwindOptions.preserveNullAndEmptyArrays(Boolean.FALSE));

        var replaceRootToInstallation = Aggregates.replaceRoot("$installations");
        var concat=  new Document("$concat", Arrays.asList(
                "$project",".","$organisation",".","$_id"));

       var project=Aggregates.project(new Document("path",concat));
       var exclude=Aggregates.project(Projections.exclude("_id"));
       var group =Aggregates.group( "$_id", Accumulators.push("path", "$path"));
       var optional = Optional.ofNullable(projectRepository.getMongoCollection()
                .aggregate(List.of(unwind,unwindInstallations,replaceRoot,replaceRootToInstallation,project,exclude, group))
                .first());

       if (optional.isPresent()) {
           return optional.get().getList("path", String.class);
       } else {
            return Collections.emptyList();
       }
    }

    public PanacheQuery<InstallationProjection> searchInstallations(Bson searchDoc, int page, int size) {

        var unwind = Aggregates
                .unwind("$providers");

        var replaceRoot = Aggregates
                .replaceRoot("$providers");

        var unwindInstallations = Aggregates.unwind("$providers.installations");

        var replaceRootToInstallation = Aggregates.replaceRoot("$installations");

        var lookup = Aggregates.lookup("MetricDefinition", "unit_of_access", "_id", "unit_of_access");

        var lookupCapacity = Aggregates.lookup("Capacity", "_id", "installation_id", "capacities");

        var installations= projectRepository.getMongoCollection()
                .aggregate(List.of(unwind, unwindInstallations, replaceRoot, replaceRootToInstallation, Aggregates.match(searchDoc),Aggregates.skip(size * (page)), Aggregates.limit(size), lookup, lookupCapacity), InstallationProjection.class)
                .into(new ArrayList<>());

        var count = projectRepository.getMongoCollection()
                .aggregate(List.of(unwind, replaceRoot, unwindInstallations, replaceRootToInstallation,Aggregates.match(searchDoc),Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<InstallationProjection>();

        projectionQuery.list = installations;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public PanacheQuery<MetricDefinition> fetchAllMetricDefinitions(String id, int page, int size){

        var eq = Aggregates
                .match(Filters.eq("installation_id", id));

        var addField = new Document("$addFields", new Document("metric_definition_id", new Document("$toObjectId", "$metric_definition_id")));

        var group = Aggregates.group("_id", Accumulators.addToSet("metric_definition_id","$metric_definition_id"));

        var lookup = Aggregates.lookup("MetricDefinition", "metric_definition_id", "_id", "metric_definition_id");

        var unwind = Aggregates.unwind("$metric_definition_id");

        var replaceRoot = Aggregates.replaceRoot("$metric_definition_id");

        var metricDefinitions = metricRepository
                .getMongoCollection()
                .aggregate(List.of(eq, addField, group, Aggregates.skip(size * (page)), Aggregates.limit(size), lookup, unwind, replaceRoot), MetricDefinition.class)
                .into(new ArrayList<>());

        var count = metricRepository
                .getMongoCollection()
                .aggregate(List.of(eq, addField, group, unwind, Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<MetricDefinition>();

        projectionQuery.list = metricDefinitions;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public boolean resourceExists(String resourceId) {

        return projectRepository.getMongoCollection().countDocuments(Filters.eq("providers.installations.resource", resourceId)) > 0;
    }

    public InstallationReport installationReport(Installation installation, String sstart, String send, Array<Bson> filters) {

        var metricDefs = metricRepository
                .getMongoCollection()
                .distinct("metric_definition_id",
                        Filters.regex("resource_id","^"+ installation.getProject() + HierarchicalRelation.PATH_SEPARATOR + installation.getOrganisation() + HierarchicalRelation.PATH_SEPARATOR + installation.getId() + "(?:\\.[^\\r\\n.]+)*$")
                        , String.class)
                .into(new ArrayList<>());

        var results = new ArrayList<MetricGroupResults>();

        for (String defId : metricDefs) {

            var metricDefinition = metricDefinitionRepository.findById(new ObjectId(defId));

            var capacities = capacityRepository
                    .getMongoCollection()
                    .find(Filters.and(
                                    Filters.eq("installation_id", installation.getId()),
                                    Filters.eq("metric_definition_id", defId)
                            ), Capacity.class)
                    .sort(Sorts.ascending("registered_on"))
                    .into(new ArrayList<>());

            var start = Utility.stringToInstant(sstart);
            var end = Utility.stringToInstant(send);

            var periods = new ArrayList<CapacityPeriod>();
            var previous = start;

            if(capacities.isEmpty()){

                var p = new CapacityPeriod();
                p.setFrom(start);
                p.setTo(end);
                p.setCapacityValue(null);
                periods.add(p);
            } else {

                if (start.isBefore(capacities.get(0).getRegisteredOn())) {

                    var firstCapDate = capacities.get(0).getRegisteredOn();
                    var next = firstCapDate.isBefore(end) ? firstCapDate : end;
                    var p = new CapacityPeriod();
                    p.setFrom(previous);
                    p.setTo(next);
                    p.setCapacityValue(null);
                    periods.add(p);
                    previous = next;
                }

                for (int i = 0; i < capacities.size(); i++) {

                    if((i < capacities.size() - 1) && end.isBefore(capacities.get(i + 1).getRegisteredOn())){

                        var p = new CapacityPeriod();
                        p.setFrom(previous);
                        p.setTo(end);
                        p.setCapacityValue(capacities.get(i).getValue());
                        periods.add(p);
                        break;
                    }

                    var next = (i < capacities.size() - 1)
                            ? capacities.get(i + 1).getRegisteredOn()
                            : end;

                    if (!next.isAfter(previous)){
                        continue;
                    }

                    var p = new CapacityPeriod();
                    p.setFrom(previous);
                    p.setTo(next);
                    p.setCapacityValue(capacities.get(i).getValue());
                    periods.add(p);
                    previous = next;
                }
            }

            for (var p : periods) {

                var totalValueObj = metricRepository
                        .getMongoCollection()
                        .aggregate(Arrays.asList(Aggregates.match(Filters.and(filters.appendAll(List.of(Filters.eq("metric_definition_id", defId), Filters.and(Filters.gte("time_period_start", p.getFrom()), Filters.lte("time_period_start", p.getTo())), Filters.and(Filters.gte("time_period_end", p.getFrom()), Filters.lte("time_period_end", p.getTo())))))), Aggregates.group(null, Accumulators.sum("total", "$value"))))
                        .map(doc -> doc.getDouble("total"))
                        .first();

                double totalValue = totalValueObj != null ? totalValueObj : 0.0;

                p.setTotalValue(totalValue);

                p.setUsagePercentage((p.getCapacityValue() == null || p.getCapacityValue().compareTo(BigDecimal.ZERO) == 0) ? null : BigDecimal.valueOf(p.getTotalValue()).divide(p.getCapacityValue(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
            }

            var group = new MetricGroupResults();
            group.setMetricDefinitionId(defId);
            group.setMetricDescription(metricDefinition.getMetricDescription());
            group.setMetricName(metricDefinition.getMetricName());
            group.setMetricType(metricDefinition.getMetricType());
            group.setUnitType(metricDefinition.getUnitType());
            group.setPeriods(periods);
            results.add(group);
        }

        var report = new InstallationReport();
        report.data = results;
        report.project = installation.getProject();
        report.provider = installation.getOrganisation();
        report.infrastructure = installation.getInfrastructure();
        report.installation = installation.getInstallation();
        report.resource = installation.getResource() == null ? "" : installation.getResource();
        report.installationId = installation.getId();
        report.externalId = installation.getExternalId();

        return report;
    }
}
