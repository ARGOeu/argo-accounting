package org.accounting.system.repositories.metric;

import org.accounting.system.entities.Metric;
import org.accounting.system.entities.acl.PermissionAccessControl;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MetricAccessControlRepository extends AccessControlModulator<Metric, ObjectId, PermissionAccessControl> {

}
