package org.accounting.system.schedulers;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.accounting.system.clients.ProviderClient;
import org.accounting.system.clients.responses.eoscportal.EOSCProvider;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class ProviderScheduler {

    @Inject
    @RestClient
    ProviderClient providerClient;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    Logger LOG;

    void onStart(@Observes StartupEvent ev) {
        execute();
    }

    @Scheduled(cron = "{provider.cron.expr}")
    void cronJobWithExpressionInConfig() {
        execute();
    }

    /**
     * This method fetches the available Providers from EOSC-Portal
     * and saves them in the Accounting System database.
     */
    public void execute(){

        retrieveEoscProviders();
    }

    /**
     * This method fetches the available Providers from EOSC-Portal.
     */
    private void retrieveEoscProviders(){

        LOG.info("Retrieving the total number of EOSC Providers.");

        var asyncTotalResponse = providerClient.getTotalNumberOfProviders("all");

        asyncTotalResponse
                .thenCompose(total -> {
                    LOG.infof("There are %s Providers in EOSC Portal.", total.total);
                    LOG.info("Retrieving the available Providers from EOSC-Portal.");

                    return providerClient.getAll("all", total.total);})
                .thenAccept(response -> saveOrUpdateProviders(response.results))
                .exceptionally(ex -> {
                    LOG.error("Failed to communicate with EOSC-Portal. ", ex);
                    return null;
                });
    }

    /**
     * This method saves or updates the EOSC Providers in the Accounting System database.
     */
    private void saveOrUpdateProviders(List<EOSCProvider> providers){

        LOG.infof("Saving or updating the %s EOSC Providers in the database.", providers != null ? providers.size() : 0);
        providerRepository.persistOrUpdate(ProviderMapper.INSTANCE.eoscProvidersToProviders(providers));
    }
}
