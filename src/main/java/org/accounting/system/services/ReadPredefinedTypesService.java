package org.accounting.system.services;

import org.accounting.system.parsetypes.ParseType;
import org.accounting.system.parsetypes.MetricType;
import org.accounting.system.parsetypes.UnitType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class ReadPredefinedTypesService {

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
        return unitType.searchForType(unitFile, type);
    }

    public String getMetricTypes(){
        return metricType.getTypes(metricFile);
    }

    public Optional<String> searchForMetricType(String type){
        return metricType.searchForType(metricFile, type);
    }
}
