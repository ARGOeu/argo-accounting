package org.accounting.system.repositories.metricdefinition;

import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.entities.acl.PermissionAccessControl;
import org.accounting.system.repositories.modulators.AccessAlwaysModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MetricDefinitionAccessAlwaysRepository extends AccessAlwaysModulator<MetricDefinition, ObjectId, PermissionAccessControl> {

}
