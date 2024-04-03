package org.accounting.system.repositories.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ForbiddenException;
import org.accounting.system.entities.client.Client;
import org.accounting.system.repositories.modulators.AccessibleModulator;

import java.util.Set;

@ApplicationScoped
public class ClientAccessEntityRepository extends AccessibleModulator<Client, String> {

    /**
     * Delegates the execution to clientAccessControlRepository since you cannot assign roles to yourself.
     */
    public Client assignRolesToRegisteredClient (String clientId, Set<String> roles){

        if(isIdentifiable(clientId)){
            throw new ForbiddenException("You cannot assign roles to yourself.");
        }

        throw new ForbiddenException("You have no access to execute this operation.");
    }

    /**
     * Delegates the execution to clientAccessControlRepository since you cannot detach roles from yourself.
     */
    public Client detachRolesFromRegisteredClient (String clientId, Set<String> roles){

        if(isIdentifiable(clientId)){
            throw new ForbiddenException("You cannot detach roles from yourself.");
        }

        throw new ForbiddenException("You have no access to execute this operation.");
    }
}
