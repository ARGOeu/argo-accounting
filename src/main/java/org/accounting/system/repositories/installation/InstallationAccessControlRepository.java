package org.accounting.system.repositories.installation;

import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.repositories.acl.AccessControlRepository;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.services.authorization.RoleService;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InstallationAccessControlRepository extends AccessControlModulator<Installation, ObjectId, RoleAccessControl> {

    @Inject
    RoleService roleService;

    @Inject
    AccessControlRepository accessControlRepository;


    public boolean accessibility(String project, String provider, String installation, Collection collection, Operation operation){

        var accessControl= accessControlRepository.findByWhoAndCollectionAndEntity(getRequestInformation().getSubjectOfToken(), Collection.Installation, project + HierarchicalRelation.PATH_SEPARATOR + provider + HierarchicalRelation.PATH_SEPARATOR + installation);

        return accessControl.filter(roleAccessControl -> roleService.hasRoleAccess(roleAccessControl.getRoles(), collection, operation)).isPresent();
    }

}
