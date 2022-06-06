package org.accounting.system.repositories.installation;

import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import org.accounting.system.dtos.installation.UpdateInstallationRequestDto;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.exceptions.ConflictException;
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
            exist(entity.getInfrastructure(), entity.getInstallation());
        }

        return super.updateEntity(entity, new ObjectId(id));
    }

    public void exist(String infrastructure, String installation){

        var optional = find("infrastructure = ?1 and installation = ?2", infrastructure, installation)
                .withCollation(Collation.builder().locale("en")
                        .collationStrength(CollationStrength.SECONDARY).build())
                .stream()
                .findAny();

        optional.ifPresent(storedInstallation -> {throw new ConflictException("There is an Installation with infrastructure "+infrastructure+" and installation "+installation+". Its id is "+storedInstallation.getId().toString());});
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
