package org.accounting.system.repositories.project;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.pivovarit.function.ThrowingFunction;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.vavr.collection.Array;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.accounting.system.dtos.project.UpdateProjectRequest;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MetricReportProjection;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.entities.projections.ProjectReport;
import org.accounting.system.entities.projections.ProviderReport;
import org.accounting.system.entities.projections.normal.ProjectProjection;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.enums.RelationType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProjectRepository extends ProjectModulator {

    @Inject
    ProviderRepository providerRepository;

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    @Inject
    InstallationRepository installationRepository;

    @Inject
    MetricRepository metricRepository;

    public PanacheQuery<ProjectProjection> fetchAll(int page, int size) {

        var projects = getMongoCollection()
                .aggregate(List.of(Aggregates.skip(size * (page)), Aggregates.limit(size)), ProjectProjection.class)
                .into(new ArrayList<>());

        Document count = getMongoCollection()
                .aggregate(List.of(Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<ProjectProjection>();

        projectionQuery.list = projects;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public PanacheQuery<ProjectProjection> fetchAllForSystemAdmin(int page, int size) {

        var projects = getMongoCollection()
                .aggregate(List.of(Aggregates.skip(size * (page)), Aggregates.limit(size)), ProjectProjection.class)
                .into(new ArrayList<>());

        Document count = getMongoCollection()
                .aggregate(List.of(Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<ProjectProjection>();

        projectionQuery.list = projects;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public List<Provider> fetchProjectProviders(String projectId){

        var eq = Aggregates
                .match(Filters.eq("_id", projectId));

        var unwind = Aggregates
                .unwind("$providers");

        var replaceRoot = Aggregates
                .replaceRoot("$providers");

        return getMongoCollection()
                .aggregate(List.of(eq, unwind, replaceRoot), Provider.class)
                .into(new ArrayList<>());
    }

    public PanacheQuery<InstallationProjection> fetchProjectInstallations(String projectId, int page, int size){

        var eq = Aggregates
                .match(Filters.eq("_id", projectId));

        var unwindProviders = Aggregates
                .unwind("$providers");

        var replaceRootProviders = Aggregates
                .replaceRoot("$providers");

        var unwindInstallations = Aggregates
                .unwind("$installations");

        var replaceRootInstallations = Aggregates
                .replaceRoot("$installations");

        Bson lookup = Aggregates.lookup("MetricDefinition", "unit_of_access", "_id", "unit_of_access");

        var installations = getMongoCollection()
                .aggregate(List.of(eq, unwindProviders, replaceRootProviders, unwindInstallations, replaceRootInstallations, Aggregates.skip(size * (page)), Aggregates.limit(size), lookup), InstallationProjection.class)
                .into(new ArrayList<>());

        Document count = getMongoCollection()
                .aggregate(List.of(eq, unwindProviders, replaceRootProviders, unwindInstallations, replaceRootInstallations, Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<InstallationProjection>();

        projectionQuery.list = installations;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public Set<Provider> associateProjectWithProviders(String projectId, Set<String> providerIds){

        var providerIdsToSet = new HashSet<>(providerIds);

        var existingIds = fetchProjectProviders(projectId)
                .stream()
                .map(Provider::getId)
                .collect(Collectors.toSet());

        providerIdsToSet.removeAll(existingIds);

        var providers = providerIdsToSet
                .stream()
                .map(ThrowingFunction.sneaky(id-> {

                    var optional = providerRepository.findByIdOptional(id).orElseThrow(()->new NotFoundException("There is no Provider with the following id: "+id));
                    return optional;
                }))
                .collect(Collectors.toSet());

        providers
                .forEach(provider -> {

                    HierarchicalRelation project = new HierarchicalRelation(projectId, RelationType.PROJECT);
                    HierarchicalRelation hprovider = new HierarchicalRelation(provider.getId(), project, RelationType.PROVIDER, provider.getId());

                    hierarchicalRelationRepository.save(project);
                    hierarchicalRelationRepository.save(hprovider);
                });

        setProjectProviders(projectId, providers);

        return providers;
    }

    public void setProjectProviders(String projectId, Set<Provider> providers){

        var providersToList = new ArrayList<>();
        providersToList.addAll(providers);

        var update = Updates.addEachToSet("providers", providersToList);

        var eq = Filters.eq("_id",  projectId);

        getMongoCollection().updateOne(eq, update);
    }

    public void deleteProjectProviders(String projectId, Set<String> providerIds){

        var query = new Document().append("_id", projectId);

        var fields = new Document().append("providers", Filters.in("_id", providerIds));

        var update = new Document("$pull", fields);

        getMongoCollection().updateOne(query, update);
    }

    public void dissociateProviderFromProject(String projectId, Set<String> providerIds){

        for(String provider: providerIds){

            providerRepository.findByIdOptional(provider).orElseThrow(()->new NotFoundException("There is no Provider with the following id: "+provider));

            var installations = installationRepository.fetchInstallationProviders(projectId, provider);

            if(!installations.isEmpty()){
                throw new ConflictException("Dissociation is not allowed. There are registered installations to {"+projectId+", "+provider+"}.");
            }
        }

        for(String provider: providerIds){
            hierarchicalRelationRepository.delete("_id = ?1",projectId + HierarchicalRelation.PATH_SEPARATOR + provider);
        }

        deleteProjectProviders(projectId, providerIds);
    }

    public ProjectProjection fetchById(String projectId){

        var eq = Aggregates
                .match(Filters.eq("_id", projectId));

        return getMongoCollection()
                .aggregate(List.of(eq), ProjectProjection.class)
                .first();
    }

    public ProjectProjection updateProject(UpdateProjectRequest request, String id) {

        var eq = Filters.eq("_id",  id);

        var list = new ArrayList<Bson>();

        if(StringUtils.isNotEmpty(request.acronym)){

            list.add(Updates.set("acronym", request.acronym));
        }

        if(StringUtils.isNotEmpty(request.title)){

            list.add(Updates.set("title", request.title));
        }

        if(StringUtils.isNotEmpty(request.startDate)){

            list.add(Updates.set("startDate", request.startDate));
        }

        if(StringUtils.isNotEmpty(request.endDate)){

            list.add(Updates.set("endDate", request.endDate));
        }

        if(StringUtils.isNotEmpty(request.callIdentifier)){

            list.add(Updates.set("callIdentifier", request.callIdentifier));
        }

        var update = Updates.combine(
                list
        );

        getMongoCollection().updateOne(eq, update);

        return fetchById(id);
    }

    public PanacheQuery<ProjectProjection> searchProjects(Bson searchDoc, int page, int size) {

        var projects = getMongoCollection()
                .aggregate(List.of(Aggregates.skip(size * (page)), Aggregates.limit(size)), ProjectProjection.class)
                .into(new ArrayList<>());

        Document count = getMongoCollection()
                .aggregate(List.of(Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<ProjectProjection>();

        projectionQuery.list = projects;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public PanacheQuery<MetricDefinition> fetchAllMetricDefinitions(String id, int page, int size){

        var eq = Aggregates
                .match(Filters.eq("project_id", id));

        var addField = new Document("$addFields", new Document("metric_definition_id", new Document("$toObjectId", "$metric_definition_id")));

        var group = Aggregates.group("_id", Accumulators.addToSet("metric_definition_id","$metric_definition_id"));

        var lookup = Aggregates.lookup("MetricDefinition", "metric_definition_id", "_id", "metric_definition_id");

        var unwind = Aggregates.unwind("$metric_definition_id");

        var replaceRoot = Aggregates.replaceRoot("$metric_definition_id");

        var metricDefinitions = metricRepository
                .getMongoCollection()
                .aggregate(List.of(eq, addField, group, Aggregates.skip(size * (page)), Aggregates.limit(size), lookup, unwind, replaceRoot), MetricDefinition.class)
                .into(new ArrayList<>());

        Document count = metricRepository
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

    public ProjectReport projectReport(String projectId, String start, String end){

        var filters = Array.of(Filters.regex("resource_id", "\\b" + projectId + "\\b"+"(?![-])"),
                Filters.and(Filters.gte("time_period_start", Utility.stringToInstant(start)), Filters.lte("time_period_start", Utility.stringToInstant(end))),
                Filters.and(Filters.gte("time_period_end", Utility.stringToInstant(start)), Filters.lte("time_period_end", Utility.stringToInstant(end))));

        var addField = new Document("$addFields", new Document("metric_definition_id", new Document("$toObjectId", "$metric_definition_id")));

        var groupId = new Document("providerId", "$provider").append("installationId", "$installation_id").append("metricDefinitionId", "$metric_definition_id");

        var group = Aggregates.group(groupId, Accumulators.sum("totalValue", "$value"));

        var extractFields = Aggregates.addFields(
                new Field<>("providerId", "$_id.providerId"),
                new Field<>("installationId", "$_id.installationId"),
                new Field<>("metricDefinitionId", "$_id.metricDefinitionId")
        );

        var lookup = Aggregates.lookup("MetricDefinition", "metricDefinitionId", "_id", "metric_definition");

        var unwind = Aggregates.unwind("$metric_definition");

        var projection = Aggregates.project(Projections.fields(
                Projections.include("installationId"),
                Projections.computed("metricDefinitionId", new Document("$toString", "$metricDefinitionId")),
                Projections.computed("metricName", "$metric_definition.metric_name"),
                Projections.computed("metricDescription", "$metric_definition.metric_description"),
                Projections.computed("unitType", "$metric_definition.unit_type"),
                Projections.computed("metricType", "$metric_definition.metric_type"),
                Projections.include("totalValue")
        ));

        var regex = Aggregates.match(Filters.and(filters));

        var finalGroup = Aggregates.group(
                "$installationId",
                Accumulators.push("data", new Document()
                        .append("metricDefinitionId", "$metricDefinitionId")
                        .append("totalValue", "$totalValue")
                        .append("metricName", "$metricName")
                        .append("metricDescription", "$metricDescription")
                        .append("unitType", "$unitType")
                        .append("metricType", "$metricType"))
        );

        var lookupInstallation = new Document("$lookup",
                new Document("from", "Project")
                        .append("let", new Document("installationId", "$_id"))
                        .append("pipeline", List.of(
                                new Document("$unwind", "$providers"),
                                new Document("$unwind", "$providers.installations"),
                                new Document("$match", new Document("$expr",
                                        new Document("$eq", List.of("$$installationId", "$providers.installations._id"))
                                )),
                                new Document("$replaceRoot", new Document("newRoot", "$providers.installations"))
                        ))
                        .append("as", "installation")
        );

        var unwindInstallation = Aggregates.unwind("$installation");

        var finalProjection = Aggregates.project(Projections.fields(
                Projections.computed("project", "$installation.project"),
                Projections.computed("provider", "$installation.organisation"),
                Projections.computed("infrastructure", "$installation.infrastructure"),
                Projections.computed("installation", "$installation.installation"),
                Projections.computed("installationId", "$installation._id"),
                Projections.computed("resource", new Document("$ifNull", List.of("$installation.resource", ""))),
                Projections.include("data")
        ));


        var groupByProvider = Aggregates.group("$provider",
                Accumulators.push("data", new Document()
                        .append("installationId", "$installationId")
                        .append("project", "$project")
                        .append("provider", "$provider")
                        .append("installation", "$installation")
                        .append("infrastructure", "$infrastructure")
                        .append("resource", "$resource")
                        .append("data", "$data")
                ),
                Accumulators.push("allMetrics", "$data")
        );

        var lookupProvider = Aggregates.lookup("Provider", "_id", "_id", "provider");

        var unwindProvider = Aggregates.unwind("$provider");

        var finalProviderProjection = Aggregates.project(Projections.fields(
                Projections.computed("provider_id", "$provider._id"),
                Projections.computed("abbreviation", new Document("$ifNull", List.of("$provider.abbreviation", ""))),
                Projections.computed("logo", new Document("$ifNull", List.of("$provider.logo", ""))),
                Projections.computed("name", "$provider.name"),
                Projections.computed("website", new Document("$ifNull", List.of("$provider.website", ""))),
                Projections.include("data"),
                Projections.computed("aggregatedMetrics", new Document("$reduce", new Document()
                        .append("input", "$allMetrics")
                        .append("initialValue", new ArrayList<>())
                        .append("in", new Document("$concatArrays", Arrays.asList("$$value", "$$this")))
                ))));

        var agg = Aggregates.unwind("$aggregatedMetrics");

        var gagg = Aggregates.group(new Document("provider_id", "$provider_id").append("metric_definition_id", "$aggregatedMetrics.metricDefinitionId"), Accumulators.sum("totalValue", "$aggregatedMetrics.totalValue"),
                Accumulators.first("metricDefinitionId", "$aggregatedMetrics.metricDefinitionId"),
                Accumulators.first("metricName", "$aggregatedMetrics.metricName"),
                Accumulators.first("metricDescription", "$aggregatedMetrics.metricDescription"),
                Accumulators.first("metricType", "$aggregatedMetrics.metricType"),
                Accumulators.first("unitType", "$aggregatedMetrics.unitType"),
                Accumulators.first("name", "$name"),
                Accumulators.first("website", "$website"),
                Accumulators.first("abbreviation", "$abbreviation"),
                Accumulators.first("logo", "$logo"),
                Accumulators.first("data", "$data")
        );

        var gpafggg = Aggregates.project(Projections.fields(
                Projections.computed("provider_id", "$_id.provider_id"),
                Projections.include("name", "website", "abbreviation", "logo", "data", "unitType", "metricType", "metricDescription", "metricName", "metricDefinitionId", "totalValue")
        ));

        var gpagg = Aggregates.group(
                "$provider_id",
                Accumulators.first("name", "$name"),
                Accumulators.first("website", "$website"),
                Accumulators.first("abbreviation", "$abbreviation"),
                Accumulators.first("logo", "$logo"),
                Accumulators.first("data", "$data"),
                Accumulators.first("provider_id", "$provider_id"),
                Accumulators.push("aggregatedMetrics", new Document()
                        .append("metricDefinitionId", "$_id.metric_definition_id")
                        .append("metricName", "$metricName")
                        .append("metricDescription", "$metricDescription")
                        .append("unitType", "$unitType")
                        .append("metricType", "$metricType")
                        .append("totalValue", "$totalValue")
                )
        );

        var data = metricRepository.getMongoCollection()
                .aggregate(List.of(regex, addField, group, extractFields,
                        lookup, unwind, projection, finalGroup,
                        lookupInstallation, unwindInstallation,
                        finalProjection, groupByProvider, lookupProvider, unwindProvider,
                        finalProviderProjection, agg, gagg, gpafggg, gpagg), ProviderReport.class).into(new ArrayList<>());

        var project = fetchById(projectId);

        var aggregatedMetrics = data
                .stream()
                .flatMap(provider->provider.aggregatedMetrics.stream())
                .collect(Collectors.toMap(k-> k.metricDefinitionId,
                m -> {
                    var copy = new MetricReportProjection();
                    copy.metricDefinitionId = m.metricDefinitionId;
                    copy.metricName = m.metricName;
                    copy.metricDescription = m.metricDescription;
                    copy.unitType = m.unitType;
                    copy.metricType = m.metricType;
                    copy.totalValue = m.totalValue;
                    return copy;
                },
                (m1, m2) -> {
                    m1.totalValue = m1.totalValue + m2.totalValue;
                    return m1;
                }
        ));

        var report = new ProjectReport();
        report.data = data;
        report.id = projectId;
        report.acronym = project.acronym;
        report.callIdentifier = project.callIdentifier;
        report.title = project.title;
        report.startDate = project.startDate;
        report.endDate = project.endDate;
        report.aggregatedMetrics = new ArrayList<>(aggregatedMetrics.values());

        return  report;
    }
}
