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
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.dtos.project.UpdateProjectRequest;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.entities.projections.ProjectReport;
import org.accounting.system.entities.projections.ProviderReport;
import org.accounting.system.entities.projections.normal.ProjectProjection;
import org.accounting.system.entities.projections.permissions.ProjectProjectionWithPermissions;
import org.accounting.system.entities.projections.permissions.ProjectionInstallationWithPermissions;
import org.accounting.system.entities.projections.permissions.ProviderProjectionWithPermissions;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.enums.RelationType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.authorization.RoleService;
import org.accounting.system.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    RoleService roleService;

    @Inject
    MetricRepository metricRepository;

    @Inject
    RequestUserContext requestUserContext;

    public PanacheQuery<ProjectProjection> fetchAll(int page, int size) {

        //TODO We have to create an index for the following query
        var eqAccessControl = Aggregates
                .match(Filters
                        .and(Filters.eq("roleAccessControls.who", requestUserContext.getId()),
                                Filters.eq("roleAccessControls.roles.collections_access_permissions.collection", Collection.Project.name()),
                                Filters.eq("roleAccessControls.roles.collections_access_permissions.access_permissions.operation", Operation.READ.name()),
                                Filters.eq("roleAccessControls.roles.collections_access_permissions.access_permissions.access_type", AccessType.ALWAYS.name())));

        var projects = getMongoCollection()
                .aggregate(List.of(eqAccessControl, Aggregates.skip(size * (page)), Aggregates.limit(size)), ProjectProjection.class)
                .into(new ArrayList<>());

        Document count = getMongoCollection()
                .aggregate(List.of(eqAccessControl, Aggregates.count()))
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

    public Optional<RoleAccessControl> fetchRoleAccessControl(String id, String who) {

        var eq = Aggregates
                .match(Filters.eq("_id", id));

        var unwind = Aggregates
                .unwind("$roleAccessControls");

        var replaceRoot = Aggregates
                .replaceRoot("$roleAccessControls");

        var eqWho = Aggregates
                .match(Filters.eq("who", who));

        return Optional.ofNullable(getMongoCollection()
                .aggregate(List.of(eq, unwind, replaceRoot, eqWho), RoleAccessControl.class)
                .first());
    }

    public PanacheQuery<RoleAccessControl> fetchAllRoleAccessControls(String id, int page, int size){

        var eq = Aggregates
                .match(Filters.eq("_id", id));

        var unwind = Aggregates
                .unwind("$roleAccessControls");

        var replaceRoot = Aggregates
                .replaceRoot("$roleAccessControls");

        var roleAccessControls = getMongoCollection()
                .aggregate(List.of(eq, unwind, replaceRoot, Aggregates.skip(size * (page)), Aggregates.limit(size)), RoleAccessControl.class)
                .into(new ArrayList<>());

        Document count = getMongoCollection()
                .aggregate(List.of(eq, unwind, replaceRoot, Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<RoleAccessControl>();

        projectionQuery.list = roleAccessControls;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public List<RoleAccessControl> fetchAllRoleAccessControls(String id){

        var eq = Aggregates
                .match(Filters.eq("_id", id));

        var unwind = Aggregates
                .unwind("$roleAccessControls");

        var replaceRoot = Aggregates
                .replaceRoot("$roleAccessControls");

        return getMongoCollection()
                .aggregate(List.of(eq, unwind, replaceRoot), RoleAccessControl.class)
                .into(new ArrayList<>());
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

    public void associateProjectWithProviders(String projectId, Set<String> providerIds){

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
                    HierarchicalRelation hprovider = new HierarchicalRelation(provider.getId(), project, RelationType.PROVIDER);

                    hierarchicalRelationRepository.save(project);
                    hierarchicalRelationRepository.save(hprovider);
                });

        setProjectProviders(projectId, providers);
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

    public boolean accessibility(String projectId, Collection collection, Operation operation){

        var roleAccessControls = fetchRoleAccessControl(projectId, requestUserContext.getId());

        List<AccessType> accessTypeList = roleAccessControls
                .stream()
                .flatMap(acl->acl.getRoles().stream())
                .map(Role::getCollectionsAccessPermissions)
                .flatMap(java.util.Collection::stream)
                .filter(cp->cp.collection.equals(collection))
                .map(cp->cp.accessPermissions)
                .flatMap(java.util.Collection::stream)
                .filter(permission -> permission.operation.equals(operation))
                .map(permission -> permission.accessType)
                .collect(Collectors.toList());

        var precedence = AccessType.higherPrecedence(accessTypeList);

        return precedence.access;
    }

    public void insertNewRoleAccessControl(String projectId, RoleAccessControl roleAccessControl){

        var query = new Document().append("_id",  projectId);

        var add = Updates.addToSet("roleAccessControls", roleAccessControl);

        getMongoCollection().updateOne(query, add);
    }

    public void insertListOfRoleAccessControl(String projectId, Set<RoleAccessControl> roleAccessControls){

        var query = new Document().append("_id",  projectId);

        for(RoleAccessControl roleAccessControl: roleAccessControls){

            var add = Updates.addToSet("roleAccessControls", roleAccessControl);

            getMongoCollection().updateOne(query, add);
        }
    }

    public void updateRoleAccessControl(String projectId, String who, Set<Role> roles){

        var update = Updates.set("roleAccessControls.$.roles", roles);

        var eq = Filters.and(Filters.eq("_id",  projectId), Filters.eq("roleAccessControls.who",  who));

        getMongoCollection().updateOne(eq, update);
    }

    public void deleteRoleAccessControl(String projectId, String who){

        var query = new Document().append("_id", projectId);
        var fields = new Document().append("roleAccessControls", new Document().append( "who", who));
        var update = new Document("$pull", fields);

        getMongoCollection().updateOne(query, update);
    }

    public PanacheQuery<ProjectProjection> searchProjects(Bson searchDoc, int page, int size) {

        //TODO We have to create an index for the following query
        var eqAccessControl = Aggregates
                .match(Filters
                        .and(searchDoc,Filters.eq("roleAccessControls.who", requestUserContext.getId()),
                                Filters.eq("roleAccessControls.roles.collections_access_permissions.collection", Collection.Project.name()),
                                Filters.eq("roleAccessControls.roles.collections_access_permissions.access_permissions.operation", Operation.READ.name()),
                                Filters.eq("roleAccessControls.roles.collections_access_permissions.access_permissions.access_type", AccessType.ALWAYS.name())));

        var projects = getMongoCollection()
                .aggregate(List.of(eqAccessControl, Aggregates.skip(size * (page)), Aggregates.limit(size)), ProjectProjection.class)
                .into(new ArrayList<>());

        Document count = getMongoCollection()
                .aggregate(List.of(eqAccessControl, Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<ProjectProjection>();

        projectionQuery.list = projects;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public PanacheQuery<ProjectProjectionWithPermissions> fetchClientPermissions(int page, int size){

        var eqAccessControl =Aggregates.match(Filters.or(
                Filters.and(Filters.eq("roleAccessControls.who", requestUserContext.getId()),
                        Filters.eq("roleAccessControls.roles.collections_access_permissions.collection", Collection.Installation.name()),
                        Filters.eq("roleAccessControls.roles.collections_access_permissions.access_permissions.operation", Operation.READ.name()),
                        Filters.eq("roleAccessControls.roles.collections_access_permissions.access_permissions.access_type", AccessType.ALWAYS.name())),

                Filters.and(Filters.eq("providers.roleAccessControls.who", requestUserContext.getId()),
                        Filters.eq("providers.roleAccessControls.roles.collections_access_permissions.collection", Collection.Installation.name()),
                        Filters.eq("providers.roleAccessControls.roles.collections_access_permissions.access_permissions.operation", Operation.READ.name()),
                        Filters.eq("providers.roleAccessControls.roles.collections_access_permissions.access_permissions.access_type", AccessType.ALWAYS.name())),

                Filters.and(Filters.eq("providers.installations.roleAccessControls.who", requestUserContext.getId()),
                        Filters.eq("providers.installations.roleAccessControls.roles.collections_access_permissions.collection", Collection.Installation.name()),
                        Filters.eq("providers.installations.roleAccessControls.roles.collections_access_permissions.access_permissions.operation", Operation.READ.name()),
                        Filters.eq("providers.installations.roleAccessControls.roles.collections_access_permissions.access_permissions.access_type", AccessType.ALWAYS.name()))));

        var projects= projectRepository.getMongoCollection()
                .aggregate(List.of(eqAccessControl, Aggregates.skip(size * (page)), Aggregates.limit(size)),  ProjectProjectionWithPermissions.class)
                .into(new ArrayList<>());

        Document count = projectRepository.getMongoCollection()
                .aggregate(List.of(eqAccessControl,Aggregates.count()))
                .first();

        for(ProjectProjectionWithPermissions project: projects){

            project.roleAccessControls.removeIf(roleAccessControl -> !roleAccessControl.getWho().equals(requestUserContext.getId()));
            project.permissions = roleService.mergeRoles(project.roleAccessControls.stream().flatMap(acl->acl.getRoles().stream()).collect(Collectors.toSet()));

            for (ProviderProjectionWithPermissions provider: project.providers){

                provider.roleAccessControls.removeIf(roleAccessControl -> !roleAccessControl.getWho().equals(requestUserContext.getId()));
                provider.permissions = roleService.mergeRoles(provider.roleAccessControls.stream().flatMap(acl->acl.getRoles().stream()).collect(Collectors.toSet()));

                for (ProjectionInstallationWithPermissions installation: provider.installations){

                    installation.roleAccessControls.removeIf(roleAccessControl -> !roleAccessControl.getWho().equals(requestUserContext.getId()));
                    installation.permissions = roleService.mergeRoles(installation.roleAccessControls.stream().flatMap(acl->acl.getRoles().stream()).collect(Collectors.toSet()));
                }
            }
        }

        for(ProjectProjectionWithPermissions project: projects){

            if(project.permissions.isEmpty()){

                var copyProviders = new ArrayList<>(project.providers);

                for (ProviderProjectionWithPermissions provider: project.providers){

                    if(provider.permissions.isEmpty() && provider.installations.stream().allMatch(installation->installation.permissions.isEmpty())){
                           copyProviders.remove(provider);
                    }
                }

            project.providers = copyProviders;

            }
        }

        for(ProjectProjectionWithPermissions project: projects){

            if(project.permissions.isEmpty()){

                for (ProviderProjectionWithPermissions provider: project.providers){

                    if(provider.permissions.isEmpty()){

                        var copyInstallations = new ArrayList<>(provider.installations);

                        for (ProjectionInstallationWithPermissions installation: provider.installations){

                            if(installation.permissions.isEmpty()){
                                copyInstallations.remove(installation);
                            }
                        }

                        provider.installations = copyInstallations;
                    }
                }
            }
        }

        var projectionQuery = new MongoQuery<ProjectProjectionWithPermissions>();

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
                )
        );

        var lookupProvider = Aggregates.lookup("Provider", "_id", "_id", "provider");

        var unwindProvider = Aggregates.unwind("$provider");

        var finalProviderProjection = Aggregates.project(Projections.fields(
                Projections.computed("provider_id", "$provider._id"),
                Projections.computed("abbreviation", new Document("$ifNull", List.of("$provider.abbreviation", ""))),
                Projections.computed("logo", new Document("$ifNull", List.of("$provider.logo", ""))),
                Projections.computed("name", "$provider.name"),
                Projections.computed("website", new Document("$ifNull", List.of("$provider.website", ""))),
                Projections.include("data")
        ));


        var data = metricRepository.getMongoCollection()
                .aggregate(List.of(regex, addField, group, extractFields,
                        lookup, unwind, projection, finalGroup,
                        lookupInstallation, unwindInstallation,
                        finalProjection, groupByProvider, lookupProvider, unwindProvider,
                        finalProviderProjection), ProviderReport.class).into(new ArrayList<>());

        var project = fetchById(projectId);

        var report = new ProjectReport();
        report.data = data;
        report.id = projectId;
        report.acronym = project.acronym;
        report.callIdentifier = project.callIdentifier;
        report.title = project.title;
        report.startDate = project.startDate;
        report.endDate = project.endDate;

        return  report;
    }
}
