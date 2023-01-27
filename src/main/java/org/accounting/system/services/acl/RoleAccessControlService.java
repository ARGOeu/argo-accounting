package org.accounting.system.services.acl;

import org.accounting.system.dtos.acl.role.RoleAccessControlRequestDto;
import org.accounting.system.dtos.acl.role.RoleAccessControlResponseDto;
import org.accounting.system.dtos.acl.role.RoleAccessControlUpdateDto;
import org.accounting.system.dtos.pagination.PageResource;

import javax.ws.rs.core.UriInfo;

public interface RoleAccessControlService {


    /**
     * This method grants specific client the roles encapsulated to {@link RoleAccessControlRequestDto Access Control Entry}.
     *
     * @param id      The entity id to which permissions will be assigned.
     * @param who     To whom the permissions will be granted.
     * @param request The roles
     */
     void grantPermission(String who, RoleAccessControlRequestDto request, String... id);


    /**
     * Modifies roles and stores them in the database.
     *
     * @param id        For which Entity will the permissions be modified.
     * @param who       To whom belongs the permissions which will be modified.
     * @param updateDto The permissions which will be modified.
     */
     RoleAccessControlResponseDto modifyPermission(String who, RoleAccessControlUpdateDto updateDto, String... id);


    /**
     * Deletes specific privileges from the database.
     *
     * @param id  The entity for which permissions will be deleted.
     * @param who The client id for which the permissions will be deleted.
     */
     void deletePermission(String who, String... id);

    /**
     * Fetches the Access Control that has been created for the given entity id and who
     *
     * @param id  The entity for which permissions will be returned.
     * @param who The client id for which the permissions will be returned.
     */
     RoleAccessControlResponseDto fetchPermission(String who, String... id);


    /**
     * Fetches all Access Controls that have been created for the given entity id
     *
     * @param id         The entity for which permissions will be returned.
     */
    PageResource<RoleAccessControlResponseDto> fetchAllPermissions(int page, int size, UriInfo uriInfo, String... id);
}
