package org.accounting.system.repositories.client;

import org.accounting.system.entities.client.Client;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import java.util.Set;

@ApplicationScoped
public class ClientAccessEntityRepository extends AccessEntityModulator<Client, String> {

    @Inject
    ClientAccessControlRepository clientAccessControlRepository;

    /**
     * Delegates the execution to clientAccessControlRepository since you cannot assign roles to yourself.
     */
    public Client assignRolesToRegisteredClient (String clientId, Set<String> roles){

        if(isIdentifiable(clientId)){
            throw new ForbiddenException("You cannot assign roles to yourself.");
        }

        return clientAccessControlRepository.assignRolesToRegisteredClient(clientId, roles);
    }

    /**
     * Delegates the execution to clientAccessControlRepository since you cannot detach roles from yourself.
     */
    public Client detachRolesFromRegisteredClient (String clientId, Set<String> roles){

        if(isIdentifiable(clientId)){
            throw new ForbiddenException("You cannot detach roles from yourself.");
        }

        return clientAccessControlRepository.detachRolesFromRegisteredClient(clientId, roles);
    }

    @Override
    public AccessControlModulator<Client, String> accessControlModulator() {
        return clientAccessControlRepository;
    }
}
