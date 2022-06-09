package org.accounting.system.repositories.client;

import org.accounting.system.entities.client.Client;
import org.accounting.system.repositories.modulators.AccessControlModulator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClientAccessControlRepository extends AccessControlModulator<Client, String> {

}
