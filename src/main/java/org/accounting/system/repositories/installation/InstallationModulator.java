package org.accounting.system.repositories.installation;

import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.UpdateInstallationRequestDto;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.repositories.modulators.AbstractModulator;
import org.apache.commons.lang3.StringUtils;
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

        if(!StringUtils.isAllBlank(request.installation, request.infrastructure)){
            installationAccessAlwaysRepository.exist(entity.getInfrastructure(), entity.getInstallation());
        }

        return super.updateEntity(entity, new ObjectId(id));
    }

    public Installation save(InstallationRequestDto request) {

        installationAccessAlwaysRepository.exist(request.infrastructure, request.installation);

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return installationAccessAlwaysRepository.save(request);
            case ENTITY:
                return installationAccessEntityRepository.save(request);
            default:
                return installationAccessAlwaysRepository.save(request);
        }
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
