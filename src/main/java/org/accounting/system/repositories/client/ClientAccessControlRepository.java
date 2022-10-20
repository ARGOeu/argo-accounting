package org.accounting.system.repositories.client;

import org.accounting.system.entities.client.Client;
import org.accounting.system.repositories.modulators.AccessControlModulator;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ForbiddenException;
import java.util.Set;

@ApplicationScoped
public class ClientAccessControlRepository extends AccessControlModulator<Client, String> {

    public Client assignRolesToRegisteredClient (String clientId, Set<String> roles){

        throw new ForbiddenException("You have no access to execute this operation.");
    }

    public Client detachRolesFromRegisteredClient (String clientId, Set<String> roles){

        throw new ForbiddenException("You have no access to execute this operation.");
    }
}
