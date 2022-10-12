package org.accounting.system.repositories.provider;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.provider.Provider;
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


    public PanacheQuery<MetricProjection> fetchAllMetrics(String id, int page, int size){

       return hierarchicalRelationRepository.findByExternalId(id, page, size);
    }

    @Override
    public AccessControlModulator<Provider, String, RoleAccessControl> accessControlModulator() {
        return providerAccessControlRepository;
    }
}
