package org.accounting.system.services.acl;

import org.accounting.system.dtos.acl.role.RoleAccessControlRequestDto;
import org.accounting.system.dtos.acl.role.RoleAccessControlResponseDto;
import org.accounting.system.dtos.acl.role.RoleAccessControlUpdateDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.AccessControlMapper;
import org.accounting.system.repositories.acl.AccessControlRepository;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.accounting.system.repositories.project.ProjectRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.stream.Collectors;

@ApplicationScoped
public class AccessControlService {


    @Inject
    AccessControlRepository accessControlRepository;

    @Inject
    RoleRepository roleRepository;

    @Inject
    ProjectRepository projectRepository;

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
     * @param id      The entity id to which permissions will be assigned.
     * @param who     To whom the permissions will be granted.
     * @param request The roles
     */
    public void grantPermission(String who, RoleAccessControlRequestDto request, String... id) {

        var projectID = id[0];

        var optional = projectRepository.fetchRoleAccessControl(projectID, who);

        if(optional.isPresent()){

            throw new ConflictException("There is a Project Access Control for the client : "+who);
        }

        var accessControl = AccessControlMapper.INSTANCE.requestToRoleAccessControl(request);

        accessControl.setWho(who);

        accessControl.setRoles(roleRepository.getRolesByName(request.roles));

        projectRepository.insertNewRoleAccessControl(projectID, accessControl);
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
     * @param id        For which Entity will the permissions be modified.
     * @param who       To whom belongs the permissions which will be modified.
     * @param updateDto The permissions which will be modified.
     */
    public RoleAccessControlResponseDto modifyPermission(String who, RoleAccessControlUpdateDto updateDto, String... id) {

        var projectID = id[0];

        var optional = projectRepository.fetchRoleAccessControl(projectID, who);

        optional.orElseThrow(() -> new NotFoundException("There is no Access Control."));

        projectRepository.updateRoleAccessControl(projectID, who, roleRepository.getRolesByName(updateDto.roles));

        var updated = projectRepository.fetchRoleAccessControl(projectID, who);

        var response = AccessControlMapper.INSTANCE.roleAccessControlToResponse(updated.get());

        response.roles = updated.get().getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        return response;
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
     * @param id  The entity for which permissions will be deleted.
     * @param who The client id for which the permissions will be deleted.
     */
    public void deletePermission(String who, String... id) {

        var projectID = id[0];

        var optional = projectRepository.fetchRoleAccessControl(projectID, who);

        optional.orElseThrow(() -> new NotFoundException("There is no Access Control."));

        projectRepository.deleteRoleAccessControl(projectID, who);
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
     * @param id  The entity for which permissions will be returned.
     * @param who The client id for which the permissions will be returned.
     */
    public RoleAccessControlResponseDto fetchPermission(String who, String... id) {

        var projectID = id[0];

        var optional = projectRepository.fetchRoleAccessControl(projectID, who);

        optional.orElseThrow(() -> new NotFoundException("There is no Access Control."));

        var response = AccessControlMapper.INSTANCE.roleAccessControlToResponse(optional.get());

        response.roles = optional.get().getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        return response;
    }

    /**
     * Fetches all Access Controls that have been created for the given entity id
     *
     * @param id         The entity for which permissions will be returned.
     */
    public PageResource<RoleAccessControlResponseDto> fetchAllPermissions(int page, int size, UriInfo uriInfo, String... id) {

        var projectID = id[0];

        var panacheQuery = projectRepository.fetchAllRoleAccessControls(projectID, page, size);

        var responses = panacheQuery
                .list()
                .stream()
                .map(acl->{

                    var response = AccessControlMapper.INSTANCE.roleAccessControlToResponse(acl);

                    response.roles = acl.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

                    return response;

                })
                .collect(Collectors.toList());

        return new PageResource<>(panacheQuery, responses, uriInfo);
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
//    private String buildMsg(List<String> rolesToBeAdded, List<String> rolesExisting) {
//
//
//        String message = rolesToBeAdded.stream().map(r -> {
//            if (rolesToBeAdded.indexOf(r) == 0) {
//                return "Access Control Entry successfully added for roles: " + r;
//            } else {
//                return r;
//            }
//
//        }).collect(Collectors.joining(","));
//        String msgExist = rolesExisting.stream().map(r -> {
//            if (rolesExisting.indexOf(r) == 0) {
//                return "Access Control Entry already exists for roles: " + r;
//            } else {
//                return r;
//            }
//
//        }).collect(Collectors.joining(","));
//
//        String totalMsg = "";
//        if (!message.equals("")) {
//            totalMsg = message + ".";
//        }
//        if (!msgExist.equals("")) {
//            totalMsg = totalMsg + " " + msgExist + ".";
//        }
//        return totalMsg;
//    }
}
