package org.accounting.system.repositories.metricdefinition;

import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.repositories.modulators.AccessControlModulator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MetricDefinitionAccessControlRepository extends AccessControlModulator<MetricDefinition> {

}
