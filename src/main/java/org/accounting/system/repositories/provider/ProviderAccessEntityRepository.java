package org.accounting.system.repositories.provider;

import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.projections.ProjectionQuery;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ProviderAccessEntityRepository extends AccessEntityModulator<Provider, String, RoleAccessControl> {

    @Inject
    ProviderAccessControlRepository providerAccessControlRepository;

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;


    public ProjectionQuery<MetricProjection> fetchAllMetrics(String id, int page, int size){

        var projection = hierarchicalRelationRepository.findByExternalId(id, page, size);

        return projection;
    }

    public boolean accessibility(String projectId, String providerId, Collection collection, Operation operation){

        return providerAccessControlRepository.accessibility(projectId, providerId, collection, operation);
    }

    @Override
    public AccessControlModulator<Provider, String, RoleAccessControl> accessControlModulator() {
        return providerAccessControlRepository;
    }
}
