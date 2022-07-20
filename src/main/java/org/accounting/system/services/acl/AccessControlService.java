package org.accounting.system.services.acl;

import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.acl.role.RoleAccessControlRequestDto;
import org.accounting.system.dtos.acl.role.RoleAccessControlResponseDto;
import org.accounting.system.dtos.acl.role.RoleAccessControlUpdateDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.enums.Collection;
import org.accounting.system.mappers.AccessControlMapper;
import org.accounting.system.repositories.acl.AccessControlRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;

@ApplicationScoped
public class AccessControlService {


    @Inject
    AccessControlRepository accessControlRepository;


//    /**
//     * Converts the request for {@link PermissionAccessControlRequestDto permissions} to {@link org.accounting.system.entities.acl.AccessControl} and stores it in the database.
//     *
//     * @param id The entity id to which permissions will be assigned.
//     * @param who To whom the permissions will be granted.
//     * @param request The permissions
//     */
//    public InformativeResponse grantPermission(String id, String who, PermissionAccessControlRequestDto request, Collection collection, AccessModulator repository){
//
//        var accessControl = AccessControlMapper.INSTANCE.requestToPermissionAccessControl(request);
//
//        accessControl.setEntity(id);
//
//        accessControl.setWho(who);
//
//        accessControl.setCollection(collection);
//
//        repository.grantPermission(accessControl);
//
//        var informativeResponse = new InformativeResponse();
//        informativeResponse.message = "Access Control entry has been created successfully";
//        informativeResponse.code = 200;
//
//        return informativeResponse;
//    }

    /**
     * Converts the request for {@link RoleAccessControlRequestDto permissions} to {@link org.accounting.system.entities.acl.AccessControl} and stores it in the database.
     *
     * @param id The entity id to which permissions will be assigned.
     * @param who To whom the permissions will be granted.
     * @param request The roles
     */
    public InformativeResponse grantPermission(String id, String who, RoleAccessControlRequestDto request, Collection collection){

        var accessControl = AccessControlMapper.INSTANCE.requestToRoleAccessControl(request);

        accessControl.setEntity(id);

        accessControl.setWho(who);

        accessControl.setCollection(collection);

        accessControlRepository.persist(accessControl);

        var informativeResponse = new InformativeResponse();
        informativeResponse.message = "Access Control entry has been created successfully";
        informativeResponse.code = 200;

        return informativeResponse;
    }

//    /**
//     * Modifies permissions and stores them in the database.
//     *
//     * @param id For which Entity will the permissions be modified.
//     * @param who To whom belongs the permissions which will be modified.
//     * @param updateDto The permissions which will be modified.
//     */
//    public PermissionAccessControlResponseDto modifyPermission(String id, String who, PermissionAccessControlUpdateDto updateDto, Collection collection, AccessModulator repository){
//
//        var accessControl = permissionAccessControlRepository.findByWhoAndCollectionAndEntity(who, collection, id);
//
//        AccessControlMapper.INSTANCE.updatePermissionAccessControlFromDto(updateDto, accessControl);
//
//        repository.modifyPermission(accessControl);
//
//        return AccessControlMapper.INSTANCE.permissionAccessControlToResponse(accessControl);
//    }

    /**
     * Modifies roles and stores them in the database.
     *
     * @param id For which Entity will the permissions be modified.
     * @param who To whom belongs the permissions which will be modified.
     * @param updateDto The permissions which will be modified.
     */
    public RoleAccessControlResponseDto modifyPermission(String id, String who, RoleAccessControlUpdateDto updateDto, Collection collection){

        var accessControl = accessControlRepository.findByWhoAndCollectionAndEntity(who, collection, id);

        accessControl.orElseThrow(()->new NotFoundException("There is no Access Control."));

        AccessControlMapper.INSTANCE.updateRoleAccessControlFromDto(updateDto, accessControl.get());

        accessControlRepository.update(accessControl.get());

        return AccessControlMapper.INSTANCE.roleAccessControlToResponse(accessControl.get());
    }

//    /**
//     * Deletes specific privileges from the database.
//     *
//     * @param id The entity for which permissions will be deleted.
//     * @param who The client id for which the permissions will be deleted.
//     */
//    public InformativeResponse deletePermission(String id, String who, Collection collection, AccessModulator repository){
//
//        var accessControl = permissionAccessControlRepository.findByWhoAndCollectionAndEntity(who, collection, id);
//
//        repository.deletePermission(accessControl);
//
//        var successResponse = new InformativeResponse();
//        successResponse.code = 200;
//        successResponse.message = "Access Control entry has been deleted successfully.";
//
//        return successResponse;
//    }

    /**
     * Deletes specific privileges from the database.
     *
     * @param id The entity for which permissions will be deleted.
     * @param who The client id for which the permissions will be deleted.
     */
    public InformativeResponse deletePermission(String id, String who, Collection collection){

        var accessControl = accessControlRepository.findByWhoAndCollectionAndEntity(who, collection, id);

        accessControl.orElseThrow(()->new NotFoundException("There is no Access Control."));

        accessControlRepository.delete(accessControl.get());

        var successResponse = new InformativeResponse();
        successResponse.code = 200;
        successResponse.message = "Access Control entry has been deleted successfully.";

        return successResponse;
    }

//    /**
//     * Fetches the Access Control that has been created for the given entity id and who
//     *
//     * @param id The entity for which permissions will be returned.
//     * @param who The client id for which the permissions will be returned.
//     */
//    public PermissionAccessControlResponseDto fetchPermission(String id, String who, AccessModulator repository){
//
//        var accessControl = repository.getPermission(id, who);
//
//        return AccessControlMapper.INSTANCE.permissionAccessControlToResponse(accessControl);
//    }

    /**
     * Fetches the Access Control that has been created for the given entity id and who
     *
     * @param id The entity for which permissions will be returned.
     * @param who The client id for which the permissions will be returned.
     */
    public RoleAccessControlResponseDto fetchPermission(String id, String who, Collection collection){

        var accessControl = accessControlRepository.findByWhoAndCollectionAndEntity(who, collection, id);

        accessControl.orElseThrow(()->new NotFoundException("There is no Access Control."));

        return AccessControlMapper.INSTANCE.roleAccessControlToResponse(accessControl.get());
    }

    /**
     * Fetches all Access Controls that have been created for the given entity id
     *
     * @param id The entity for which permissions will be returned.
     * @param collection The collection that the entity belongs to.
     */
    public PageResource<RoleAccessControlResponseDto> fetchAllPermissions(String id, Collection collection, int page, int size, UriInfo uriInfo){

        var panacheQuery = accessControlRepository.findAllByEntityAndCollection(id, collection, page,size);

        return new PageResource<>(panacheQuery, AccessControlMapper.INSTANCE.roleAccessControlsToResponse(panacheQuery.list()), uriInfo);
    }

//    /**
//     * Fetches all Access Control that have been created for the given repository
//     **/
//    public List<PermissionAccessControlResponseDto> fetchAllPermissions(AccessModulator repository){
//
//        var accessControl = repository.getAllPermissions();
//
//        return AccessControlMapper.INSTANCE.permissionAccessControlsToResponse(accessControl);
//    }
}
