package org.accounting.system.services.acl;

import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.acl.AccessControlRequestDto;
import org.accounting.system.dtos.acl.AccessControlResponseDto;
import org.accounting.system.dtos.acl.AccessControlUpdateDto;
import org.accounting.system.enums.Collection;
import org.accounting.system.mappers.AccessControlMapper;
import org.accounting.system.repositories.acl.AccessControlRepository;
import org.accounting.system.repositories.modulators.AccessModulator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class AccessControlService {

    @Inject
    AccessControlRepository accessControlRepository;

    /**
     * Converts the request for {@link AccessControlRequestDto permissions} to {@link org.accounting.system.entities.acl.AccessControl} and stores it in the database.
     *
     * @param id The entity id to which permissions will be assigned.
     * @param who To whom the permissions will be granted.
     * @param request The permissions
     */
    public InformativeResponse grantPermission(String id, String who, AccessControlRequestDto request, Collection collection, AccessModulator repository){

        var accessControl = AccessControlMapper.INSTANCE.requestToAccessControl(request);

        accessControl.setEntity(id);

        accessControl.setWho(who);

        accessControl.setCollection(collection);

        repository.grantPermission(accessControl);

        var informativeResponse = new InformativeResponse();
        informativeResponse.message = "Access Control entry has been created successfully";
        informativeResponse.code = 200;

        return informativeResponse;
    }

    /**
     * Modifies permissions and stores them in the database.
     *
     * @param id For which Entity will the permissions be modified.
     * @param who To whom belongs the permissions which will be modified.
     * @param updateDto The permissions which will be modified.
     */
    public AccessControlResponseDto modifyPermission(String id, String who, AccessControlUpdateDto updateDto, Collection collection, AccessModulator repository){

        var accessControl = accessControlRepository.findByWhoAndCollectionAndEntity(who, collection, id);

        AccessControlMapper.INSTANCE.updateAccessControlFromDto(updateDto, accessControl);

        repository.modifyPermission(accessControl);

        return AccessControlMapper.INSTANCE.accessControlToResponse(accessControl);
    }

    /**
     * Deletes specific privileges from the database.
     *
     * @param id The entity for which permissions will be deleted.
     * @param who The client id for which the permissions will be deleted.
     */
    public InformativeResponse deletePermission(String id, String who, Collection collection, AccessModulator repository){

        var accessControl = accessControlRepository.findByWhoAndCollectionAndEntity(who, collection, id);

        repository.deletePermission(accessControl);

        var successResponse = new InformativeResponse();
        successResponse.code = 200;
        successResponse.message = "Access Control entry has been deleted successfully.";

        return successResponse;
    }

    /**
     * Fetches the Access Control that has been created for the given entity id and who
     *
     * @param id The entity for which permissions will be returned.
     * @param who The client id for which the permissions will be returned.
     */
    public AccessControlResponseDto fetchPermission(String id, String who, AccessModulator repository){

        var accessControl = repository.getPermission(id, who);

        return AccessControlMapper.INSTANCE.accessControlToResponse(accessControl);
    }

    /**
     * Fetches all Access Control that have been created for the given repository
     **/
    public List<AccessControlResponseDto> fetchAllPermissions(AccessModulator repository){

        var accessControl = repository.getAllPermissions();

        return AccessControlMapper.INSTANCE.accessControlsToResponse(accessControl);
    }
}
