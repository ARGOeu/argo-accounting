package org.accounting.system.repositories.provider;

import org.accounting.system.entities.provider.Provider;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ProviderAccessEntityRepository extends AccessEntityModulator<Provider, String> {

    @Inject
    ProviderAccessControlRepository providerAccessControlRepository;

    @Override
    public AccessControlModulator<Provider, String> accessControlModulator() {
        return providerAccessControlRepository;
    }
}
