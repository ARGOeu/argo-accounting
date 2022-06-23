package org.accounting.system.lifecycle;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class ApplicationLifeCycle {

    @Inject
    Logger LOG;

    void onStart(@Observes StartupEvent ev) {
        LOG.info("The application is starting with profile " + ProfileManager.getActiveProfile());
    }
}
