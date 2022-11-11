package org.accounting.system.repositories.project;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.pivovarit.function.ThrowingFunction;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.oidc.TokenIntrospection;
import io.quarkus.panache.common.Page;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MongoQuery;
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
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
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
    TokenIntrospection tokenIntrospection;

    @Inject
    RoleService roleService;

    @Inject
    MetricRepository metricRepository;

    @ConfigProperty(name = "key.to.retrieve.id.from.access.token")
    String key;

    public PanacheQuery<ProjectProjection> fetchAll(int page, int size) {

        //TODO We have to create an index for the following query
        var eqAccessControl = Aggregates
                .match(Filters
                        .and(Filters.eq("roleAccessControls.who", tokenIntrospection.getJsonObject().getString(key)),
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

    public boolean accessibility(String projectId, Collection collection, Operation operation){

        var roleAccessControls = fetchRoleAccessControl(projectId, tokenIntrospection.getJsonObject().getString(key));

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
                        .and(searchDoc,Filters.eq("roleAccessControls.who", tokenIntrospection.getJsonObject().getString(key)),
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
                Filters.and(Filters.eq("roleAccessControls.who", tokenIntrospection.getJsonObject().getString(key)),
                        Filters.eq("roleAccessControls.roles.collections_access_permissions.collection", Collection.Installation.name()),
                        Filters.eq("roleAccessControls.roles.collections_access_permissions.access_permissions.operation", Operation.READ.name()),
                        Filters.eq("roleAccessControls.roles.collections_access_permissions.access_permissions.access_type", AccessType.ALWAYS.name())),

                Filters.and(Filters.eq("providers.roleAccessControls.who", tokenIntrospection.getJsonObject().getString(key)),
                        Filters.eq("providers.roleAccessControls.roles.collections_access_permissions.collection", Collection.Installation.name()),
                        Filters.eq("providers.roleAccessControls.roles.collections_access_permissions.access_permissions.operation", Operation.READ.name()),
                        Filters.eq("providers.roleAccessControls.roles.collections_access_permissions.access_permissions.access_type", AccessType.ALWAYS.name())),

                Filters.and(Filters.eq("providers.installations.roleAccessControls.who", tokenIntrospection.getJsonObject().getString(key)),
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

            project.roleAccessControls.removeIf(roleAccessControl -> !roleAccessControl.getWho().equals(tokenIntrospection.getJsonObject().getString(key)));
            project.permissions = roleService.mergeRoles(project.roleAccessControls.stream().flatMap(acl->acl.getRoles().stream()).collect(Collectors.toSet()));

            for (ProviderProjectionWithPermissions provider: project.providers){

                provider.roleAccessControls.removeIf(roleAccessControl -> !roleAccessControl.getWho().equals(tokenIntrospection.getJsonObject().getString(key)));
                provider.permissions = roleService.mergeRoles(provider.roleAccessControls.stream().flatMap(acl->acl.getRoles().stream()).collect(Collectors.toSet()));

                for (ProjectionInstallationWithPermissions installation: provider.installations){

                    installation.roleAccessControls.removeIf(roleAccessControl -> !roleAccessControl.getWho().equals(tokenIntrospection.getJsonObject().getString(key)));
                    installation.permissions = roleService.mergeRoles(installation.roleAccessControls.stream().flatMap(acl->acl.getRoles().stream()).collect(Collectors.toSet()));
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
}
