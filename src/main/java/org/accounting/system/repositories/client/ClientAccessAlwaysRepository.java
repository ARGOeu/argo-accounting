package org.accounting.system.repositories.client;

import org.accounting.system.entities.client.Client;
import org.accounting.system.repositories.modulators.AccessAlwaysModulator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClientAccessAlwaysRepository extends AccessAlwaysModulator<Client, String> {
}
