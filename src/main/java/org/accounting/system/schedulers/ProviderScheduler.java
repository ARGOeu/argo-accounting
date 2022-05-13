package org.accounting.system.schedulers;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import org.accounting.system.clients.ProviderClient;
import org.accounting.system.clients.responses.eoscportal.EOSCProvider;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.ProviderRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
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

        var eoscProviders = retrieveEoscProviders();
        saveOrUpdate(eoscProviders);
    }

    /**
     * This method fetches the available Providers from EOSC-Portal.
     */
    private List<EOSCProvider> retrieveEoscProviders(){

        LOG.info("Retrieving the total number of EOSC Providers.");

        var total = providerClient.getTotalNumberOfProviders();

        LOG.infof("There are %s Providers in EOSC Portal.", total.total);

        LOG.info("Retrieving the available Providers from EOSC-Portal.");

        var response = providerClient.getAll(total.total);

        return response.results;
    }

    /**
     * This method saves or updates the EOSC Providers in the Accounting System database.
     */
    private void saveOrUpdate(List<EOSCProvider> providers){

        LOG.info("Saving or updating the available EOSC Providers in the database.");
        providerRepository.persistOrUpdate(ProviderMapper.INSTANCE.eoscProvidersToProviders(providers));
    }
}
