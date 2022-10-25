package org.accounting.system.util;


import io.quarkus.oidc.TokenIntrospection;
import org.accounting.system.beans.RequestInformation;
import org.accounting.system.dtos.authorization.request.RoleRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.repositories.client.ClientRepository;
import org.accounting.system.services.MetricDefinitionService;
import org.apache.commons.validator.GenericValidator;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class Utility {

    @Inject
    MetricDefinitionService metricDefinitionService;

    @Inject
    RequestInformation requestInformation;

    @Inject
    TokenIntrospection tokenIntrospection;

    @ConfigProperty(name = "key.to.retrieve.id.from.access.token")
    String id;

    @Inject
    ClientRepository clientRepository;

    public void collectionHasTheAppropriatePermissions(RoleRequestDto request) {

        request
                .collectionsAccessPermissions
                .stream()
                .filter(collectionAccessPermissionDto -> {
                    Set<Operation> availableCollectionOperations = Collection.valueOf(collectionAccessPermissionDto.collection).availableOperations();

                    Set<Operation> operationsFromRequest = collectionAccessPermissionDto.accessPermissions.stream().flatMap(accessPermissionDto -> Stream.of(accessPermissionDto.operation)).map(Operation::valueOf).collect(Collectors.toSet());

                    operationsFromRequest.removeAll(availableCollectionOperations);

                    return !operationsFromRequest.isEmpty();
                })
                .findAny()
                .ifPresent(optional -> {
                    String message = String.format("The collection "+optional.collection +" doesn't support one or more of given operations");
                    throw new BadRequestException(message);
                });
    }

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

    public Set<String> getRoles(){

        String vopersonId = tokenIntrospection.getJsonObject().getString(id);

        // sub -> Usually a machine-readable identifier of the resource owner who authorized this token
        requestInformation.setSubjectOfToken(tokenIntrospection.getJsonObject().getString(id));

        return clientRepository.getClientRoles(vopersonId);
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

    public static boolean isBefore(String start, String end){

        if(stringToInstant(start).isBefore(stringToInstant(end))){
            return true;
        } else{
            throw new BadRequestException("start cannot be after end.");
        }
    }

    public void setAccessToken(){

        // sub -> Usually a machine-readable identifier of the resource owner who authorized this token
        requestInformation.setSubjectOfToken(tokenIntrospection.getJsonObject().getString(id));
    }

    public String getIdFromToken(String chunk) throws ParseException {


        Base64.Decoder decoder = Base64.getUrlDecoder();

        String payload = new String(decoder.decode(chunk));

        JSONParser parser = new JSONParser();

        JSONObject json = (JSONObject) parser.parse(payload);

        return (String) json.get("sub");
    }
}