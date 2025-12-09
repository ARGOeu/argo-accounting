package org.accounting.system.repositories.project;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.accounting.system.dtos.project.UpdateProjectRequest;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.entities.projections.ProjectProjectionWithPermissions;
import org.accounting.system.entities.projections.normal.ProjectProjection;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.enums.RelationType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
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

        var count = getMongoCollection()
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

        var lookup = Aggregates.lookup("MetricDefinition", "unit_of_access", "_id", "unit_of_access");

        var installations = getMongoCollection()
                .aggregate(List.of(eq, unwindProviders, replaceRootProviders, unwindInstallations, replaceRootInstallations, Aggregates.skip(size * (page)), Aggregates.limit(size), lookup), InstallationProjection.class)
                .into(new ArrayList<>());

        var count = getMongoCollection()
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

    public void associateProjectWithProvider(String projectId, String providerId){

        var existingIds = fetchProjectProviders(projectId)
                .stream()
                .map(Provider::getId)
                .collect(Collectors.toSet());

        if(existingIds.contains(providerId)){

            throw new ConflictException("This provider has already been associated with the Project : "+projectId);
        }

        var provider = providerRepository.findById(providerId);

        var project = new HierarchicalRelation(projectId, RelationType.PROJECT);
        var hprovider = new HierarchicalRelation(provider.getId(), project, RelationType.PROVIDER, provider.getExternalId());

        hierarchicalRelationRepository.save(project);
        hierarchicalRelationRepository.save(hprovider);

        setProjectProviders(projectId, Set.of(provider));
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

    public void dissociateProviderFromProject(String projectId, String providerId){

        var installations = installationRepository.fetchInstallationProviders(projectId, providerId);

        if(!installations.isEmpty()){
            throw new ConflictException("Dissociation is not allowed. There are registered installations to {"+projectId+", "+providerId+"}.");
        }

        hierarchicalRelationRepository.delete("_id = ?1",projectId + HierarchicalRelation.PATH_SEPARATOR + providerId);

        deleteProjectProviders(projectId, Set.of(providerId));
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

    public ProjectProjectionWithPermissions fetchClientPermissions(String id){

        var eq = Aggregates
                .match(Filters.eq("_id", id));

        return projectRepository
                .getMongoCollection()
                .aggregate(List.of(eq),  ProjectProjectionWithPermissions.class)
                .first();

    }

    public ProjectProjectionWithPermissions fetchClientPermissions(String projectId, String providerId){

        var match = Aggregates
                .match(Filters.eq("_id", projectId));

        var projection = Aggregates.project(
                Projections.fields(
                        Projections.include("_id", "acronym", "title", "start_date", "end_date", "call_identifier"),
                        Projections.fields(
                Projections.computed("providers",
                        new Document("$filter", new Document()
                                .append("input", "$providers")
                                .append("as", "p")
                                .append("cond", new Document("$eq", Arrays.asList("$$p._id", providerId)))
                        )
                )
        )));


        return projectRepository
                .getMongoCollection()
                .aggregate(List.of(match, projection),  ProjectProjectionWithPermissions.class)
                .first();
    }

    public ProjectProjectionWithPermissions fetchClientPermissions(String projectId, String providerId, String installationId){

        var pipeline = Arrays.asList(
                Aggregates.match(Filters.eq("_id", projectId)),

                Aggregates.project(
                        Projections.fields(
                                Projections.include("_id", "acronym", "title", "start_date", "end_date", "call_identifier"),
                                Projections.computed("providers",
                                        new Document("$map", new Document()
                                                .append("input",
                                                        new Document("$filter", new Document()
                                                                .append("input", "$providers")
                                                                .append("as", "p")
                                                                .append("cond", new Document("$eq", Arrays.asList("$$p._id", providerId)))
                                                        )
                                                )
                                                .append("as", "p")
                                                .append("in",
                                                        new Document("_id", "$$p._id")
                                                                .append("name", "$$p.name")
                                                                .append("website", "$$p.website")
                                                                .append("abbreviation", "$$p.abbreviation")
                                                                .append("logo", "$$p.logo")
                                                                .append("external_id", "$$p.external_id")
                                                                .append("installations",
                                                                        new Document("$filter", new Document()
                                                                                .append("input", "$$p.installations")
                                                                                .append("as", "i")
                                                                                .append("cond", new Document("$eq", Arrays.asList("$$i._id", installationId)))
                                                                        )
                                                                )
                                                )
                                        )
                                )
                        )
                )
        );

        return projectRepository
                .getMongoCollection()
                .aggregate(pipeline,  ProjectProjectionWithPermissions.class)
                .first();
    }

    public PanacheQuery<ProjectProjectionWithPermissions> fetchClientPermissions(int page, int size){


        var projects= projectRepository.getMongoCollection()
                .aggregate(List.of(Aggregates.skip(size * (page)), Aggregates.limit(size)),  ProjectProjectionWithPermissions.class)
                .into(new ArrayList<>());

        Document count = projectRepository.getMongoCollection()
                .aggregate(List.of(Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<ProjectProjectionWithPermissions>();

        projectionQuery.list = projects;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

}
