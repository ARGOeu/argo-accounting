package org.accounting.system.repositories.client;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ForbiddenException;
import org.accounting.system.entities.client.Client;
import org.accounting.system.enums.ApiMessage;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * {@link ClientRepository This repository} encapsulates the logic required to access
 * {@link Client} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link Client} collection. It is also responsible for mapping
 * the data from the storage format to the {@link Client}.
 *
 * Since {@link ClientRepository this repository} extends {@link ClientModulator},
 * it has access to all queries, which determine the degree of accessibility of the data.
 *
 * Also, all the operations that are defined on {@link PanacheMongoRepository} are available on this repository.
 * In this repository, we essentially define the queries that will be executed on the database without any restrictions.
 */
@ApplicationScoped
public class ClientRepository extends ClientModulator {

    @ConfigProperty(name = "api.accounting.system.admin.vopersonid", defaultValue = "")
    String vopersonId;

    @ConfigProperty(name = "api.accounting.system.admin.name", defaultValue = "")
    String name;

    @ConfigProperty(name = "api.accounting.system.admin.email", defaultValue = "")
    String email;

    public boolean isSystemAdmin(String vopersonId){

        var client = findByIdOptional(vopersonId);

        if(client.isEmpty()){

            return false;
        } else if(!client.get().isSystemAdmin()){

            return false;
        } else {

            return true;
        }
    }

    public Client getFirstSystemAdmin(){
        return find("systemAdmin = ?1", Boolean.TRUE).stream().findFirst().get();
    }

    public void addSystemAdmins(){

        String[] ids = Arrays.stream(vopersonId.split(",")).map(String::trim).toArray(String[]::new);
        String[] names = Arrays.stream(name.split(",")).map(String::trim).toArray(String[]::new);
        String[] emails = Arrays.stream(email.split(",")).map(String::trim).toArray(String[]::new);

        for(int i=0; i<ids.length; i++){
           addSystemAdmin(ids[i], names[i], emails[i]);
        }
    }

    public void addSystemAdmin(String vopersonId, String name, String email){

        findByIdOptional(vopersonId).ifPresentOrElse(client -> {

            client.setName(name);
            client.setEmail(email);
            update(client);

        }, ()->{

            var client = new Client();

            client.setSystemAdmin(true);
            client.setEmail(email);
            client.setName(name);
            client.setId(vopersonId);
            client.setRoles(Set.of("collection_owner"));
            client.setRegisteredOn(LocalDateTime.now());
            persist(client);
        });
    }

    public Set<String> getClientRoles(String vopersonId){

        var client = findByIdOptional(vopersonId);

        if (client.isPresent()){
            return client.get().getRoles();
        } else {
            throw new ForbiddenException(ApiMessage.UNAUTHORIZED_CLIENT.message);
        }
    }

    public List<Document> lookup(int page, int size){

        Bson bson = Aggregates.lookup("RoleAccessControl", "_id", "who", "who");

        List<Document> projections = getMongoCollection()
                .aggregate(List
                        .of(bson,
                                Aggregates.skip(size * (page)),
                                Aggregates.limit(size)
                        ))
                .into(new ArrayList<>());

        return projections;
    }

    public long countDocuments(Date start, Date end) {

        return getMongoCollection().countDocuments(Filters.and(Filters.gte("registeredOn", start), Filters.lte("registeredOn", adjustEndDate(end))));
    }

    /**
     * Adjusts the given date to the end of the day (23:59:59.999).
     * @param date The original end date.
     * @return A new Date set to 23:59:59.999 of the given day.
     */
    private Date adjustEndDate(Date date) {

        var calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * Updates an existing client's details in the database.
     *
     * @param clientID  The unique identifier of the client.
     * @param name The client's name.
     * @param email The client's email.
     * @param registeredOn The client's registered datetime.
     * @return {@code true} if the update was successful, {@code false} otherwise.
     */
    public boolean updateClient(String clientID, String name, String email, LocalDateTime registeredOn) {

        Bson filter = Filters.eq("_id", clientID);

        var list = new ArrayList<Bson>();

        if(StringUtils.isNotEmpty(name)){

            list.add(Updates.set("name", name));
        }

        if(StringUtils.isNotEmpty(email)){

            list.add(Updates.set("email", email));
        }

        if(!Objects.isNull(registeredOn)){

            list.add(Updates.set("registeredOn", registeredOn));
        }

        Bson updates = Updates.combine(list);

        return getMongoCollection().updateOne(filter, updates).getModifiedCount() > 0;
    }
}