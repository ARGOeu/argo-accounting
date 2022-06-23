package org.accounting.system.repositories.provider;

import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.repositories.acl.AccessControlRepository;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.services.authorization.RoleService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ProviderAccessControlRepository extends AccessControlModulator<Provider, String, RoleAccessControl> {

    @Inject
    RoleService roleService;

    @Inject
    AccessControlRepository accessControlRepository;

    public boolean accessibility(String projectId, String providerId, Collection collection, Operation operation){

        var accessControl= accessControlRepository.findByWhoAndCollectionAndEntity(getRequestInformation().getSubjectOfToken(), Collection.Provider, projectId + HierarchicalRelation.PATH_SEPARATOR + providerId);

        return accessControl.filter(roleAccessControl -> roleService.hasRoleAccess(roleAccessControl.getRoles(), collection, operation)).isPresent();
    }

//    public ProjectionQuery<InstallationProjection> lookupInstallations(String from, String localField, String foreignField, String as, int page, int size, Class<InstallationProjection> projection) {
//
//        var providers = getAccessControlRepository().findAllByWhoAndCollection(getRequestInformation().getSubjectOfToken(), collection(), AccessControlPermission.ACCESS_PROVIDER);
//
//        var projectsToString = providers
//                .stream()
//                .map(AccessControl::getEntity)
//                .collect(Collectors.toList());
//
//        Bson bson = Aggregates.lookup(from, localField, foreignField, as);
//
//        List<InstallationProjection> projections = getMongoCollection("Installation").aggregate(List.of(bson, Aggregates.skip(size * (page)), Aggregates.match(Filters.in("project", projectsToString)), Aggregates.limit(size)), projection).into(new ArrayList<>());
//
//        var projectionQuery = new ProjectionQuery<InstallationProjection>();
//
//        projectionQuery.list = projections;
//        projectionQuery.index = page;
//        projectionQuery.size = size;
//        projectionQuery.count = getMongoCollection().countDocuments();
//
//        return projectionQuery;
//    }
}
