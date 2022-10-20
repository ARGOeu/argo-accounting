package org.accounting.system.repositories.metric;

import org.accounting.system.entities.Metric;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MetricAccessEntityRepository extends AccessEntityModulator<Metric, ObjectId> {

    @Inject
    MetricAccessControlRepository metricAccessControlRepository;

    @Override
    public AccessControlModulator<Metric, ObjectId> accessControlModulator() {
        return metricAccessControlRepository;
    }
}
