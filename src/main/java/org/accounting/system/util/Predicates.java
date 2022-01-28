package org.accounting.system.util;


import org.accounting.system.dtos.MetricDefinitionRequestDto;
import org.accounting.system.services.MetricDefinitionService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Predicates {

    @Inject
    MetricDefinitionService metricDefinitionService;

    public Predicates(MetricDefinitionService metricDefinitionService){

        this.metricDefinitionService = metricDefinitionService;
    }

    public void exist(MetricDefinitionRequestDto request){

        metricDefinitionService.exist(request.unitType, request.metricName);
    }
}
