package org.accounting.system.repositories.installation;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.enums.ApiMessage;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

@ApplicationScoped
public class InstallationAccessEntityRepository extends AccessEntityModulator<Installation, ObjectId, RoleAccessControl> {

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    @Inject
    InstallationAccessControlRepository installationAccessControlRepository;

    public Installation save(InstallationRequestDto request) {
        throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
    }

    public PanacheQuery<MetricProjection> fetchAllMetrics(String id, int page, int size){

        return hierarchicalRelationRepository.findByExternalId(id, page, size);
    }

    public boolean accessibility(String project, String provider, String installation, Collection collection, Operation operation){

        return installationAccessControlRepository.accessibility(project, provider, installation, collection, operation);
    }

    @Override
    public AccessControlModulator<Installation, ObjectId, RoleAccessControl> accessControlModulator() {
        return installationAccessControlRepository;
    }
}
