package org.accounting.system.repositories.metric;

import org.accounting.system.entities.Metric;
import org.accounting.system.entities.acl.PermissionAccessControl;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MetricAccessEntityRepository extends AccessEntityModulator<Metric, ObjectId, PermissionAccessControl> {

    @Inject
    MetricAccessControlRepository metricAccessControlRepository;

    @Override
    public AccessControlModulator<Metric, ObjectId, PermissionAccessControl> accessControlModulator() {
        return metricAccessControlRepository;
    }
}
