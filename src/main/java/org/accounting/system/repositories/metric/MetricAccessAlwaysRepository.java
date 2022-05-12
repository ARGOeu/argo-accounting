package org.accounting.system.repositories.metric;

import org.accounting.system.entities.Metric;
import org.accounting.system.repositories.modulators.AccessAlwaysModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MetricAccessAlwaysRepository extends AccessAlwaysModulator<Metric, ObjectId> {
}
