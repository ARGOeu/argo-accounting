package org.accounting.system.util;


import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import org.apache.commons.validator.GenericValidator;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static java.lang.Math.min;
import static java.util.stream.Collectors.toMap;

@ApplicationScoped
public class Utility {

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

    public static boolean isDate(String... values){

        if(Arrays.stream(values).allMatch(value->GenericValidator.isDate(value, "yyyy-MM-dd", true))){
            return true;
        } else{
            throw new BadRequestException("The date format must be as follows YYYY-MM-DD");
        }
    }

    public static Instant stringToInstant(String date){

        LocalDate localDateStart = LocalDate.parse(date);
        LocalDateTime localDateTimeStart = localDateStart.atStartOfDay();
        return localDateTimeStart.toInstant(ZoneOffset.UTC);
    }

    public static Instant startToInstant(String date){

        LocalDate localDateStart = LocalDate.parse(date);
        LocalDateTime localDateTimeStart = localDateStart.atStartOfDay();
        return localDateTimeStart.toInstant(ZoneOffset.UTC);
    }

    public static Instant endToInstant(String date){

        LocalDate localDateStart = LocalDate.parse(date);
        LocalDateTime localDateTimeStart = localDateStart.atTime(23, 59, 59, 999999999);
        return localDateTimeStart.toInstant(ZoneOffset.UTC);
    }

    public static boolean isBefore(String start, String end){

        if(stringToInstant(start).isBefore(stringToInstant(end))){
            return true;
        } else{
            throw new BadRequestException("start cannot be after end.");
        }
    }

    /**
     * This method paginates a list of objects.
     *
     * @param list The list to be paginated.
     * @param pageSize The page size.
     * @return A map containing the pages of objects.
     */
    @CacheResult(cacheName = "partition")
    public <T> Map<Integer, List<T>> partition(List<T> list, int pageSize) {

        return IntStream.iterate(0, i -> i + pageSize)
                .limit((list.size() + pageSize - 1) / pageSize)
                .boxed()
                .collect(toMap(i -> i / pageSize,
                        i -> list.subList(i, min(i + pageSize, list.size()))));
    }
}