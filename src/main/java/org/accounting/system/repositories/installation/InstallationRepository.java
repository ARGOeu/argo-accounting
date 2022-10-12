package org.accounting.system.repositories.installation;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.oidc.TokenIntrospection;
import io.quarkus.panache.common.Page;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.enums.RelationType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
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
    TokenIntrospection tokenIntrospection;

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    @ConfigProperty(name = "key.to.retrieve.id.from.access.token")
    String key;

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

    public Optional<List<Installation>> fetchInstallationProviders(String projectID, String providerID){

        var project = Aggregates
                .match(Filters.eq("_id", projectID));

        var unwind = Aggregates.unwind("$providers");

        var provider = Aggregates
                .match(Filters.eq("providers._id", providerID));

        var replaceRoot = Aggregates
                .replaceRoot("$providers");

        var unwindInstallations = Aggregates.unwind("$installations");

        var replaceRootToInstallation = Aggregates.replaceRoot("$installations");

        return Optional.of(projectRepository
                .getMongoCollection()
                .aggregate(List.of(project, unwind, provider, replaceRoot, unwindInstallations, replaceRootToInstallation), Installation.class)
                .into(new ArrayList<>()));
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

        HierarchicalRelation hinstallation = new HierarchicalRelation(installation.getId().toString(), provider, RelationType.INSTALLATION);

        hierarchicalRelationRepository.save(project, null);
        hierarchicalRelationRepository.save(provider, null);
        hierarchicalRelationRepository.save(hinstallation, null);

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

        var roleAccessControls = fetchRoleAccessControl(project, provider, installation, tokenIntrospection.getJsonObject().getString(key));

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

    public List<HierarchicalRelationProjection> hierarchicalStructure(final String externalId) {

        return hierarchicalRelationRepository.hierarchicalStructure(externalId);
//        switch (getRequestInformation().getAccessType()){
//            case ALWAYS:
//                return projectAccessAlwaysRepository.hierarchicalStructure(externalId);
//            case ENTITY:
//                return projectAccessEntityRepository.hierarchicalStructure(externalId);
//            default:
//                throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
//        }
    }
}
