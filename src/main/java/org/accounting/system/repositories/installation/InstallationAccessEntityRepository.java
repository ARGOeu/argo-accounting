package org.accounting.system.repositories.installation;

import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

@ApplicationScoped
public class InstallationAccessEntityRepository extends AccessEntityModulator<Installation, ObjectId> {

    @Inject
    InstallationAccessControlRepository installationAccessControlRepository;

    public Installation save(InstallationRequestDto request) {
        throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
    }

    @Override
    public AccessControlModulator<Installation, ObjectId> accessControlModulator() {
        return installationAccessControlRepository;
    }
}
