package org.accounting.system.repositories.client;

import org.accounting.system.entities.client.Client;
import org.accounting.system.repositories.modulators.AbstractModulator;

import javax.inject.Inject;


public class ClientModulator extends AbstractModulator<Client, String> {


    @Inject
    ClientAccessEntityRepository clientAccessEntityRepository;

    @Inject
    ClientAccessAlwaysRepository clientAccessAlwaysRepository;


    @Override
    public ClientAccessAlwaysRepository always() {
        return clientAccessAlwaysRepository;
    }

    @Override
    public ClientAccessEntityRepository entity() {
        return clientAccessEntityRepository;
    }
}