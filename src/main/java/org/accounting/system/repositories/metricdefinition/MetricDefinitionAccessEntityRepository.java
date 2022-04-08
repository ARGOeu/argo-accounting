package org.accounting.system.repositories.metricdefinition;

import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.repositories.modulators.AccessEntityModulator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MetricDefinitionAccessEntityRepository extends AccessEntityModulator<MetricDefinition> {

}
