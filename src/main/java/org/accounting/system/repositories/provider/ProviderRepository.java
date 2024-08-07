package org.accounting.system.repositories.provider;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.dtos.provider.UpdateProviderRequestDto;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.entities.projections.ProviderProjectionWithProjectInfo;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.modulators.AccessibleModulator;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.services.HierarchicalRelationService;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link ProviderRepository This repository} encapsulates the logic required to access
 * {@link Provider} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link Provider} collection. It is also responsible for mapping
 * the data from the storage format to the {@link Provider}.
 *
 * Since {@link ProviderRepository this repository} extends {@link AccessibleModulator},
 * it has access to all queries, which determine the degree of accessibility of the data.
 *
 * Also, all the operations that are defined on {@link io.quarkus.mongodb.panache.PanacheMongoRepository} are available on this repository.
 * In this repository, we essentially define the queries that will be executed on the database without any restrictions.
 */
@ApplicationScoped
public class ProviderRepository extends AccessibleModulator<Provider, String> {

    @Inject
    ProjectRepository projectRepository;

    @Inject
    MetricRepository metricRepository;

    @Inject
    HierarchicalRelationService hierarchicalRelationService;

    @Inject
    RequestUserContext requestUserContext;

    /**
     * Executes a query in mongo database to fetch a Provider by given name.
     *
     * @param name The Provider name.
     * @return the Provider wrapped in an {@link Optional}.
     */
    public Optional<Provider> findByName(String name){

        return find("name = ?1", name).stream().findAny();
    }

    public Optional<RoleAccessControl> fetchRoleAccessControl(String projectID, String providerID, String who) {

        var project = Aggregates
                .match(Filters.eq("_id", projectID));

        var unwind = Aggregates.unwind("$providers");

        var provider = Aggregates
                .match(Filters.eq("providers._id", providerID));

        var replaceRoot = Aggregates
                .replaceRoot("$providers");

        var unwindAcl = Aggregates.unwind("$roleAccessControls");

        var eqWho = Aggregates
                .match(Filters.eq("roleAccessControls.who", who));

        var replaceRootToAcl = Aggregates.replaceRoot("$roleAccessControls");

        return Optional.ofNullable(projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindAcl, eqWho, unwindAcl, replaceRootToAcl), RoleAccessControl.class)
                .first());
    }

    public PanacheQuery<InstallationProjection> fetchProviderInstallations(String projectID, String providerID, int page, int size){

        var project = Aggregates
                .match(Filters.eq("_id", projectID));

        var unwind = Aggregates.unwind("$providers");

        var provider = Aggregates
                .match(Filters.eq("providers._id", providerID));

        var replaceRootProviders = Aggregates
                .replaceRoot("$providers");

        var unwindInstallations = Aggregates
                .unwind("$installations");

        var replaceRootInstallations = Aggregates
                .replaceRoot("$installations");

        Bson lookup = Aggregates.lookup("MetricDefinition", "unit_of_access", "_id", "unit_of_access");

        var installations = projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRootProviders, unwindInstallations, replaceRootInstallations, Aggregates.skip(size * (page)), Aggregates.limit(size), lookup), InstallationProjection.class)
                .into(new ArrayList<>());

        //TODO We should improve the way we count documents
        Document count = projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRootProviders, unwindInstallations, replaceRootInstallations, Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<InstallationProjection>();

        projectionQuery.list = installations;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public void insertNewRoleAccessControl(String projectID, String providerID, RoleAccessControl roleAccessControl){

        var update = Updates.push("providers.$.roleAccessControls", roleAccessControl);

        var eq = Filters.and(Filters.eq("_id",  projectID), Filters.eq("providers._id",  providerID));

        projectRepository.getMongoCollection().updateOne(eq, update);
    }

    public void updateRoleAccessControl(String projectID, String providerID, String who, Set<Role> roles){

        var update = Updates.set("providers.$[provider].roleAccessControls.$[acl].roles", roles);

        var options =new UpdateOptions();

        options.arrayFilters(List.of(Filters.eq("provider._id", providerID), Filters.eq("acl.who", who)));

        var eq = Filters.and(Filters.eq("_id",  projectID));

        projectRepository.getMongoCollection().updateOne(eq, update, options);
    }

    public void deleteRoleAccessControl(String projectID, String providerID, String who){

        var query = Filters.and(Filters.eq("_id", projectID), Filters.eq("providers._id", providerID));

        projectRepository.getMongoCollection().updateOne(query, Updates.pull("providers.$.roleAccessControls", new Document("who", who)));
    }

    public PanacheQuery<RoleAccessControl> fetchAllRoleAccessControls(String projectID, String providerID, int page, int size){

        var project = Aggregates
                .match(Filters.eq("_id", projectID));

        var unwind = Aggregates.unwind("$providers");

        var provider = Aggregates
                .match(Filters.eq("providers._id", providerID));

        var replaceRoot = Aggregates
                .replaceRoot("$providers");

        var replaceRootAcl = Aggregates
                .replaceRoot("$roleAccessControls");

        var unwindAcl = Aggregates
                .unwind("$roleAccessControls");

        var roleAccessControls = projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindAcl, replaceRootAcl, Aggregates.skip(size * (page)), Aggregates.limit(size)), RoleAccessControl.class)
                .into(new ArrayList<>());

        Document count = projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindAcl, replaceRootAcl, Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<RoleAccessControl>();

        projectionQuery.list = roleAccessControls;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public boolean accessibility(String project, String provider, Collection collection, Operation operation){

        var roleAccessControls = fetchRoleAccessControl(project, provider, requestUserContext.getId());

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

    public PanacheQuery<ProviderProjectionWithProjectInfo> fetchSystemProviders(int page, int size) {

        //TODO We have to create an index for the following query
        var eqAccessControl = Aggregates.match(Filters.or(
                Filters.and(Filters.eq("roleAccessControls.who", requestUserContext.getId()),
                        Filters.eq("roleAccessControls.roles.collections_access_permissions.collection", Collection.Provider.name()),
                        Filters.eq("roleAccessControls.roles.collections_access_permissions.access_permissions.operation", Operation.READ.name()),
                        Filters.eq("roleAccessControls.roles.collections_access_permissions.access_permissions.access_type", AccessType.ALWAYS.name())),

                Filters.and(Filters.eq("providers.roleAccessControls.who", requestUserContext.getId()),
                        Filters.eq("providers.roleAccessControls.roles.collections_access_permissions.collection", Collection.Provider.name()),
                        Filters.eq("providers.roleAccessControls.roles.collections_access_permissions.access_permissions.operation", Operation.READ.name()),
                        Filters.eq("providers.roleAccessControls.roles.collections_access_permissions.access_permissions.access_type", AccessType.ALWAYS.name())))
        );

        var unwind = Aggregates
                .unwind("$providers");
        var replaceRoot = Aggregates
                .replaceRoot(new Document("$mergeObjects", Arrays.asList(new Document("project_id", "$_id").append("project_acronym", "$acronym"), "$providers")));

        var providers= projectRepository
                .getMongoCollection()
                .aggregate(List.of(eqAccessControl, unwind, eqAccessControl, replaceRoot, Aggregates.skip(size * (page)), Aggregates.limit(size)),  ProviderProjectionWithProjectInfo.class)
                .into(new ArrayList<>());

        Document count = projectRepository
                .getMongoCollection()
                .aggregate(List.of(eqAccessControl,unwind,eqAccessControl,replaceRoot,Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<ProviderProjectionWithProjectInfo>();

        projectionQuery.list = providers;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;

    }
    public PanacheQuery<Provider> searchProviders(Bson searchDoc,int page, int size) {

        //TODO We have to create an index for the following query
        var eqAccessControl = Aggregates.match(Filters.or(
                Filters.and(Filters.eq("roleAccessControls.who", requestUserContext.getId()),
                        Filters.eq("roleAccessControls.roles.collections_access_permissions.collection", Collection.Provider.name()),
                        Filters.eq("roleAccessControls.roles.collections_access_permissions.access_permissions.operation", Operation.READ.name()),
                        Filters.eq("roleAccessControls.roles.collections_access_permissions.access_permissions.access_type", AccessType.ALWAYS.name())),

                Filters.and(Filters.eq("providers.roleAccessControls.who", requestUserContext.getId()),
                        Filters.eq("providers.roleAccessControls.roles.collections_access_permissions.collection", Collection.Provider.name()),
                        Filters.eq("providers.roleAccessControls.roles.collections_access_permissions.access_permissions.operation", Operation.READ.name()),
                        Filters.eq("providers.roleAccessControls.roles.collections_access_permissions.access_permissions.access_type", AccessType.ALWAYS.name())))
        );

        var unwind = Aggregates
                .unwind("$providers");

        var replaceRoot = Aggregates
                .replaceRoot("$providers");
        var group =Aggregates.group( "$_id", Accumulators.first("name","$name"),Accumulators.first("website","$website"),
                Accumulators.first("abbreviation","$abbreviation"),Accumulators.first("logo","$logo"));

        var providers= getMongoCollection("Project")
                .aggregate(List.of(eqAccessControl,  unwind,eqAccessControl, replaceRoot,group,Aggregates.match(searchDoc), Aggregates.skip(size * (page)), Aggregates.limit(size)),  Provider.class)
                .into(new ArrayList<>());

        Document count = getMongoCollection("Project")
                .aggregate(List.of(eqAccessControl,unwind,eqAccessControl,replaceRoot,group, Aggregates.match(searchDoc),Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<Provider>();

        projectionQuery.list = providers;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;

    }

    public PanacheQuery<MetricDefinition> fetchAllMetricDefinitions(String projectId, String providerId, int page, int size){

        var eq = Aggregates
                .match(Filters.and(Filters.eq("project_id", projectId), Filters.eq("provider", providerId)));

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

    /**
     * This method is responsible for updating a part or all attributes of existing Provider.
     *
     * @param id The Provider to be updated.
     * @param request The Provider attributes to be updated.
     * @return The updated Provider.
     * @throws ForbiddenException If Provider derives from EOSC-Portal.
     */
    public Provider updateEntity(String id, UpdateProviderRequestDto request) {

        Provider entity = findById(id);

        // if Provider's creator id is null then it derives from EOSC-Portal
        if(Objects.isNull(entity.getCreatorId())){
            throw new ForbiddenException("You cannot update a Provider which derives from EOSC-Portal.");
        }

        if(hierarchicalRelationService.providerBelongsToAnyProject(id)){
            throw new ForbiddenException("You cannot update a Provider which belongs to a Project.");
        }

        ProviderMapper.INSTANCE.updateProviderFromDto(request, entity);

        return super.updateEntity(entity, id);
    }
}
