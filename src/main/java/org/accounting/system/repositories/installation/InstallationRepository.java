package org.accounting.system.repositories.installation;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UnwindOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.enums.RelationType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    RequestUserContext requestUserContext;

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    public void exist(String projectID, String providerID, String installationID){

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

        var optional = Optional.ofNullable(projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindInstallations, eqInstallation, unwindInstallations, replaceRootToInstallation), Installation.class)
                .first());

        optional.ifPresent(storedInstallation -> {throw new ConflictException("There is an Installation with the following combination : {"+projectID+", "+providerID+", "+installationID+"}. Its id is "+storedInstallation.getId().toString());});
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

        Bson lookup = Aggregates.lookup("MetricDefinition", "unit_of_access", "_id", "unit_of_access");

        return projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindInstallations, eqInstallation, unwindInstallations, replaceRootToInstallation, lookup), InstallationProjection.class)
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

    public Installation save(InstallationRequestDto request) {

        var installation = insertNewInstallation(request);

        HierarchicalRelation project = new HierarchicalRelation(request.project, RelationType.PROJECT);

        HierarchicalRelation provider = new HierarchicalRelation(request.organisation, project, RelationType.PROVIDER);

        HierarchicalRelation hinstallation = new HierarchicalRelation(installation.getId(), provider, RelationType.INSTALLATION);

        hierarchicalRelationRepository.save(project);
        hierarchicalRelationRepository.save(provider);
        hierarchicalRelationRepository.save(hinstallation);

        return installation;
    }

    public Installation insertNewInstallation(InstallationRequestDto request){

        var installationToBeStored = InstallationMapper.INSTANCE.requestToInstallation(request);

        installationToBeStored.setId(new ObjectId().toString());

        var update = Updates.push("providers.$.installations", installationToBeStored);

        var eq = Filters.and(Filters.eq("_id",  installationToBeStored.getProject()), Filters.eq("providers._id",  installationToBeStored.getOrganisation()));

        projectRepository.getMongoCollection().updateOne(eq, update);

        return installationToBeStored;
    }

    public void insertNewRoleAccessControl(String projectID, String providerID, String installationID, RoleAccessControl roleAccessControl){

        var update = Updates.push("providers.$[provider].installations.$[installation].roleAccessControls", roleAccessControl);

        var options =new UpdateOptions();

        options.arrayFilters(List.of(Filters.eq("provider._id", providerID), Filters.eq("installation._id", installationID)));

        var eq = Filters.and(Filters.eq("_id",  projectID));

        projectRepository.getMongoCollection().updateOne(eq, update, options);
    }

    public void updateRoleAccessControl(String projectID, String providerID, String installationID, String who, Set<Role> roles){

        var update = Updates.set("providers.$[provider].installations.$[installation].roleAccessControls.$[acl].roles", roles);

        var options =new UpdateOptions();

        options.arrayFilters(List.of(Filters.eq("provider._id", providerID), Filters.eq("installation._id", installationID), Filters.eq("acl.who", who)));

        var eq = Filters.and(Filters.eq("_id",  projectID));

        projectRepository.getMongoCollection().updateOne(eq, update, options);
    }

    public void deleteRoleAccessControl(String projectID, String providerID, String installationID, String who){

        var update = Updates.pull("providers.$[provider].installations.$[installation].roleAccessControls", new Document("who", who));

        var options =new UpdateOptions();

        options.arrayFilters(List.of(Filters.eq("provider._id", providerID), Filters.eq("installation._id", installationID)));

        var eq = Filters.and(Filters.eq("_id",  projectID));

        projectRepository.getMongoCollection().updateOne(eq, update, options);
    }

    public PanacheQuery<RoleAccessControl> fetchAllRoleAccessControls(String projectID, String providerID, String installationID, int page, int size){

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

        var unwindAcl = Aggregates.unwind("$roleAccessControls");

        var replaceRootToAcl = Aggregates.replaceRoot("$roleAccessControls");

        var roleAccessControls = projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindInstallations, eqInstallation, replaceRootToInstallation, unwindAcl, replaceRootToAcl, Aggregates.skip(size * (page)), Aggregates.limit(size)), RoleAccessControl.class)
                .into(new ArrayList<>());

        Document count = projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindInstallations, eqInstallation, replaceRootToInstallation, unwindAcl, replaceRootToAcl, Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<RoleAccessControl>();

        projectionQuery.list = roleAccessControls;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public Optional<RoleAccessControl> fetchRoleAccessControl(String projectID, String providerID, String installationID, String who) {

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

        var unwindAcl = Aggregates.unwind("$roleAccessControls");

        var eqWho = Aggregates
                .match(Filters.eq("roleAccessControls.who", who));

        var replaceRootToAcl = Aggregates.replaceRoot("$roleAccessControls");

        return Optional.ofNullable(projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindInstallations, eqInstallation, replaceRootToInstallation, unwindAcl, eqWho, replaceRootToAcl), RoleAccessControl.class)
                .first());
    }

    public boolean accessibility(String project, String provider, String installation, Collection collection, Operation operation){

        var roleAccessControls = fetchRoleAccessControl(project, provider, installation, requestUserContext.getId());

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

    public PanacheQuery<InstallationProjection> fetchAllInstallations(int page, int size) {

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

        var unwind = Aggregates
                .unwind("$providers");

        var replaceRoot = Aggregates
                .replaceRoot("$providers");
        var unwindOptions = new UnwindOptions();
        var unwindInstallations = Aggregates.unwind("$providers.installations",unwindOptions.preserveNullAndEmptyArrays(Boolean.FALSE));

        var replaceRootToInstallation = Aggregates.replaceRoot("$installations");
        Bson lookup = Aggregates.lookup("MetricDefinition", "unit_of_access", "_id", "unit_of_access");

        var installations= projectRepository.getMongoCollection()
                .aggregate(List.of(eqAccessControl, unwind,unwindInstallations,eqAccessControl,replaceRoot,replaceRootToInstallation, Aggregates.skip(size * (page)), Aggregates.limit(size),lookup),  InstallationProjection.class)
                .into(new ArrayList<>());

        Document count = projectRepository.getMongoCollection()
                .aggregate(List.of(eqAccessControl,unwind,unwindInstallations,eqAccessControl,replaceRoot,replaceRootToInstallation,Aggregates.count()))
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
                .aggregate(List.of(eqAccessControl, unwind,unwindInstallations,eqAccessControl,replaceRoot,replaceRootToInstallation,project,exclude, group))
                .first());

        if(optional.isPresent()){
            return optional.get().getList("path", String.class);
        } else {
            return Collections.emptyList();
        }
    }

    public PanacheQuery<InstallationProjection> searchInstallations(Bson searchDoc, int page, int size) {

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

        var unwind = Aggregates
                .unwind("$providers");

        var replaceRoot = Aggregates
                .replaceRoot("$providers");

        var unwindInstallations = Aggregates.unwind("$providers.installations");

        var replaceRootToInstallation = Aggregates.replaceRoot("$installations");
        Bson lookup = Aggregates.lookup("MetricDefinition", "unit_of_access", "_id", "unit_of_access");

        var installations= projectRepository.getMongoCollection()
                .aggregate(List.of(eqAccessControl, unwind,unwindInstallations,eqAccessControl,replaceRoot,replaceRootToInstallation, Aggregates.match(searchDoc),Aggregates.skip(size * (page)), Aggregates.limit(size),lookup),  InstallationProjection.class)
                .into(new ArrayList<>());

        Document count = projectRepository.getMongoCollection()
                .aggregate(List.of(eqAccessControl,unwind,eqAccessControl,replaceRoot,unwindInstallations,replaceRootToInstallation,Aggregates.match(searchDoc),Aggregates.count()))
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

    public boolean resourceExists(String resourceId) {

        return projectRepository.getMongoCollection().countDocuments(Filters.eq("providers.installations.resource", resourceId)) > 0;
    }
}
