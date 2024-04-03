package org.accounting.system.repositories.client;

import com.mongodb.client.model.Filters;
import jakarta.enterprise.context.ApplicationScoped;
import org.accounting.system.entities.client.Client;
import org.accounting.system.repositories.modulators.AccessibleModulator;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Set;

@ApplicationScoped
public class ClientAccessAlwaysRepository extends AccessibleModulator<Client, String> {

    public Client assignRolesToRegisteredClient (String clientId, Set<String> roles){

        Client client = findById(clientId);

        client.getRoles().addAll(roles);

        persistOrUpdate(client);

        return client;
    }

    public Client detachRolesFromRegisteredClient (String clientId, Set<String> roles){

        Bson filter = Filters.in("roles", roles);
        Bson query = new Document().append("_id", clientId);
        Bson update = new Document("$pull", filter);

        getMongoCollection().updateOne(query, update);

        return findById(clientId);
    }
}
