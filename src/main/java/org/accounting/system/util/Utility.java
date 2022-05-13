package org.accounting.system.util;


import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.services.MetricDefinitionService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class Utility {

    @Inject
    MetricDefinitionService metricDefinitionService;


    public void exist(MetricDefinitionRequestDto request){

        metricDefinitionService.exist(request.unitType, request.metricName);
    }

    public static Set<String> getNamesSet(Class<? extends Enum<?>> e) {
        Enum<?>[] enums = e.getEnumConstants();
        String[] names = new String[enums.length];
        for (int i = 0; i < enums.length; i++) {
            names[i] = enums[i].name();
        }
        Set<String> mySet = new HashSet<String>(Arrays.asList(names));
        return mySet;
    }
}
