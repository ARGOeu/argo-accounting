package org.accounting.system.repositories.client;

import org.accounting.system.entities.client.Client;
import org.accounting.system.repositories.modulators.AbstractModulator;

import javax.inject.Inject;
import java.util.Set;


public class ClientModulator extends AbstractModulator<Client, String> {


    @Inject
    ClientAccessEntityRepository clientAccessEntityRepository;

    @Inject
    ClientAccessAlwaysRepository clientAccessAlwaysRepository;

    public Client assignRolesToRegisteredClient (String clientId, Set<String> roles){

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return clientAccessAlwaysRepository.assignRolesToRegisteredClient(clientId, roles);
            case ENTITY:
                return clientAccessEntityRepository.assignRolesToRegisteredClient(clientId, roles);
            default:
                return clientAccessAlwaysRepository.assignRolesToRegisteredClient(clientId, roles);
        }
    }

    public Client detachRolesFromRegisteredClient (String clientId, Set<String> roles){

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return clientAccessAlwaysRepository.detachRolesFromRegisteredClient(clientId, roles);
            case ENTITY:
                return clientAccessEntityRepository.detachRolesFromRegisteredClient(clientId, roles);
            default:
                return clientAccessAlwaysRepository.detachRolesFromRegisteredClient(clientId, roles);
        }
    }

    @Override
    public ClientAccessAlwaysRepository always() {
        return clientAccessAlwaysRepository;
    }

    @Override
    public ClientAccessEntityRepository entity() {
        return clientAccessEntityRepository;
    }
}