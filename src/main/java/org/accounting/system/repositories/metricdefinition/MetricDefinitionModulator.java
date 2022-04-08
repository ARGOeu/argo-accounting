package org.accounting.system.repositories.metricdefinition;

import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.repositories.modulators.AbstractModulator;

import javax.inject.Inject;


public class MetricDefinitionModulator extends AbstractModulator<MetricDefinition> {


    @Inject
    MetricDefinitionAccessEntityRepository metricDefinitionAccessEntityRepository;

    @Inject
    MetricDefinitionAccessAlwaysRepository metricDefinitionAccessAlwaysRepository;


    @Override
    public MetricDefinitionAccessAlwaysRepository always() {
        return metricDefinitionAccessAlwaysRepository;
    }

    @Override
    public MetricDefinitionAccessEntityRepository entity() {
        return metricDefinitionAccessEntityRepository;
    }
}
