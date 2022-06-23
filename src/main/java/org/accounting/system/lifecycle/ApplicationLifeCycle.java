package org.accounting.system.lifecycle;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import org.accounting.system.repositories.client.ClientRepository;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class ApplicationLifeCycle {

    @Inject
    Logger LOG;

    @Inject
    ClientRepository clientRepository;

    void onStart(@Observes StartupEvent ev) {
        LOG.info("The application is starting with profile " + ProfileManager.getActiveProfile());

        LOG.info("Adding system admins into the Accounting System");
        clientRepository.addSystemAdmins();
    }
}
