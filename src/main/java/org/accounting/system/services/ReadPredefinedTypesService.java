package org.accounting.system.services;

import io.quarkus.runtime.StartupEvent;
import io.vavr.control.Try;
import org.accounting.system.exceptions.FailToStartException;
import org.accounting.system.parsetypes.MetricType;
import org.accounting.system.parsetypes.ParseType;
import org.accounting.system.parsetypes.UnitType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Optional;

/**
 * {@link ReadPredefinedTypesService} is responsible for returning the available unit and metric types.
 * The service uses the {@link ConfigProperty} annotation to inject the unit and metric type path.
 * It also injects the {@link UnitType} and {@link MetricType}. These annotations indicate the way that the available types are parsed.
 *
 */
@ApplicationScoped
public class ReadPredefinedTypesService {

    @Inject
    UnitTypeService unitTypeService;

    @Inject
    MetricTypeService metricTypeService;

    @ConfigProperty(name = "unit.types.file")
    String unitFile;

    @UnitType
    ParseType unitType;

    @ConfigProperty(name = "metric.types.file")
    String metricFile;

    @MetricType
    ParseType metricType;

    public String getUnitTypes(){
        return unitType.getTypes(unitFile);
    }

    public Optional<String> searchForUnitType(String type){

        var databaseValue = unitTypeService.getUnitByType(type);
        var fileValue = unitType.searchForType(unitFile, type);

        if(databaseValue.isEmpty() && fileValue.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of("exist");
        }
    }

    public String getMetricTypes(){
        return metricType.getTypes(metricFile);
    }

    public Optional<String> searchForMetricType(String type){

        var databaseValue = metricTypeService.getMetricByType(type);
        var fileValue = metricType.searchForType(metricFile, type);

        if(databaseValue.isEmpty() && fileValue.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of("exist");
        }
    }

    void onStart(@Observes StartupEvent ev) {

        Try.run(()->unitType.getTypes(unitFile))
                .getOrElseThrow(()->new FailToStartException("The path of the unit file must be defined."));
        Try.run(()->metricType.getTypes(metricFile))
                .getOrElseThrow(()->new FailToStartException("The path of the metric file must be defined."));
    }
}
