package org.accounting.system.repositories.installation;

import org.accounting.system.dtos.installation.UpdateInstallationRequestDto;
import org.accounting.system.entities.Installation;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.repositories.modulators.AbstractModulator;
import org.bson.types.ObjectId;

import javax.inject.Inject;


public class InstallationModulator extends AbstractModulator<Installation, ObjectId> {


    @Inject
    InstallationAccessEntityRepository installationAccessEntityRepository;

    @Inject
    InstallationAccessAlwaysRepository installationAccessAlwaysRepository;

    /**
     * This method is responsible for updating a part or all attributes of existing Installation.
     *
     * @param id The Installation to be updated.
     * @param request The Installation attributes to be updated.
     * @return The updated Installation.
     */
    public Installation updateEntity(String id, UpdateInstallationRequestDto request) {

        Installation entity = findById(new ObjectId(id));

        InstallationMapper.INSTANCE.updateInstallationFromDto(request, entity);

        return super.updateEntity(entity, new ObjectId(id));
    }


    @Override
    public InstallationAccessAlwaysRepository always() {
        return installationAccessAlwaysRepository;
    }

    @Override
    public InstallationAccessEntityRepository entity() {
        return installationAccessEntityRepository;
    }
}
