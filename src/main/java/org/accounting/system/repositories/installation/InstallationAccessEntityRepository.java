package org.accounting.system.repositories.installation;

import org.accounting.system.entities.installation.Installation;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InstallationAccessEntityRepository extends AccessEntityModulator<Installation, ObjectId> {

    @Inject
    InstallationAccessControlRepository installationAccessControlRepository;

    @Override
    public AccessControlModulator<Installation, ObjectId> accessControlModulator() {
        return installationAccessControlRepository;
    }
}
