package org.accounting.system.repositories.metricdefinition;

import org.accounting.system.entities.MetricDefinition;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MetricDefinitionAccessEntityRepository extends AccessEntityModulator<MetricDefinition, ObjectId> {

    @Inject
    MetricDefinitionAccessControlRepository metricDefinitionAccessControlRepository;

    @Override
    public AccessControlModulator<MetricDefinition, ObjectId> accessControlModulator() {
        return metricDefinitionAccessControlRepository;
    }
}
