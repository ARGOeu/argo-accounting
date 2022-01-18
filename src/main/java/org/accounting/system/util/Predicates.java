package org.accounting.system.util;


import org.accounting.system.dtos.MetricDefinitionDtoRequest;
import org.accounting.system.services.MetricDefinitionService;
import org.accounting.system.services.ReadPredefinedTypesService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.Objects;
import java.util.function.Consumer;

@ApplicationScoped
public class Predicates {

    @Inject
    ReadPredefinedTypesService readPredefinedTypesService;

    @Inject
    MetricDefinitionService metricDefinitionService;

   public  <T> Consumer<T> emptyRequestBody(T request){

       return (any)-> {if (Objects.isNull(request)) throw new BadRequestException("The request body is empty.");};
   }

    public void noAvailableUnitType(MetricDefinitionDtoRequest request){

        if(readPredefinedTypesService.searchForUnitType(request.unitType).isEmpty()){

            throw new NotFoundException("There is no unit type : " + request.unitType);
        }
    }

    public void noAvailableMetricType(MetricDefinitionDtoRequest request){

        if(readPredefinedTypesService.searchForMetricType(request.metricType).isEmpty()){

            throw new NotFoundException("There is no metric type : " + request.metricType);
        }
    }

    public void exist(MetricDefinitionDtoRequest request){

        metricDefinitionService.exist(request.unitType, request.metricName);
    }
}
