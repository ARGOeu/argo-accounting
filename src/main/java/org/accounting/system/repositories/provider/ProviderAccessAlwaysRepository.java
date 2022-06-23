package org.accounting.system.repositories.provider;

import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.projections.ProjectionQuery;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.modulators.AccessAlwaysModulator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ProviderAccessAlwaysRepository extends AccessAlwaysModulator<Provider, String, RoleAccessControl> {

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    public ProjectionQuery<MetricProjection> fetchAllMetrics(String id, int page, int size){

        var projection = hierarchicalRelationRepository.findByExternalId(id, page, size);

        return projection;
    }

    public boolean accessibility(String projectId, String providerId){

        return true;
    }
}
