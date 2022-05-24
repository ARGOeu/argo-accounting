package org.accounting.system.util;


import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.services.MetricDefinitionService;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class Utility {

    @Inject
    MetricDefinitionService metricDefinitionService;


    public void exist(MetricDefinitionRequestDto request){

        metricDefinitionService.exist(request.unitType, request.metricName);
    }

    /**
     * This method transforms a list of ids into the given class type
     *
     * @param classType Determines the type of id
     * @param ids The List of ids to be transformed into the given class type
     * @return The transformed list
     */
    public static  <T> List<T> transformIdsToSpecificClassType(Class<?> classType, List<String> ids){

        List<T> newList = new ArrayList<>();

        ids.forEach(id-> newList.add(transformIdToSpecificClassType(classType, id)));

        return newList;
    }

    /**
     * This method transforms the id value into the given class type
     *
     * @param classType Determines the type of id
     * @param id The id to be transformed into the given class type
     * @return The transformed id
     */
    public static <T> T transformIdToSpecificClassType(Class<?> classType, String id){

        if(classType.equals(ObjectId.class)){
            return (T) new ObjectId(id);
        } else {
            return (T) id;
        }
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
