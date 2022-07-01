package org.accounting.system.repositories.metricdefinition;

import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.entities.acl.PermissionAccessControl;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MetricDefinitionAccessEntityRepository extends AccessEntityModulator<MetricDefinition, ObjectId, PermissionAccessControl> {

    @Inject
    MetricDefinitionAccessControlRepository metricDefinitionAccessControlRepository;

    @Override
    public AccessControlModulator<MetricDefinition, ObjectId, PermissionAccessControl> accessControlModulator() {
        return metricDefinitionAccessControlRepository;
    }
}
