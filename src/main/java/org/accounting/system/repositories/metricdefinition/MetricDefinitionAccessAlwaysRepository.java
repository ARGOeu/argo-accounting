package org.accounting.system.repositories.metricdefinition;

import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.repositories.modulators.AccessAlwaysModulator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MetricDefinitionAccessAlwaysRepository extends AccessAlwaysModulator<MetricDefinition> {
}
