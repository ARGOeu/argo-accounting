package org.accounting.system.repositories.client;

import org.accounting.system.entities.client.Client;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ClientAccessEntityRepository extends AccessEntityModulator<Client, String> {

    @Inject
    ClientAccessControlRepository clientAccessControlRepository;

    @Override
    public AccessControlModulator<Client, String> accessControlModulator() {
        return clientAccessControlRepository;
    }
}
