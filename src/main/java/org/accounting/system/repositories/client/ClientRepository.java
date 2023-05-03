package org.accounting.system.repositories.client;

import com.mongodb.client.model.Aggregates;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.entities.client.Client;
import org.accounting.system.enums.ApiMessage;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ForbiddenException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @ConfigProperty(name = "system.admin.vopersonid", defaultValue = "")
    String vopersonId;

    @ConfigProperty(name = "system.admin.name", defaultValue = "")
    String name;

    @ConfigProperty(name = "system.admin.email", defaultValue = "")
    String email;

    public void isSystemAdmin(String vopersonId){

        var client = findByIdOptional(vopersonId);

        if(client.isEmpty()){

            throw new ForbiddenException("You cannot perform this operation");
        } else if(!client.get().isSystemAdmin()){

            throw new ForbiddenException("You cannot perform this operation");
        }

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

            Client client = new Client();

            client.setSystemAdmin(true);
            client.setEmail(email);
            client.setName(name);
            client.setId(vopersonId);
            client.setRoles(Set.of("collection_owner"));
            persist(client);
        });
    }

    public List<Client> getSystemAdmins(){
        return find("systemAdmin = ?1", true).list();
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
}