package org.accounting.system.repositories.provider;

import org.accounting.system.entities.provider.Provider;
import org.accounting.system.repositories.modulators.AccessAlwaysModulator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProviderAccessAlwaysRepository extends AccessAlwaysModulator<Provider, String> {

}
