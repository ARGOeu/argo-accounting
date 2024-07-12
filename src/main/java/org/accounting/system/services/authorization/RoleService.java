package org.accounting.system.services.authorization;

import com.mongodb.MongoWriteException;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.dtos.authorization.CollectionAccessPermissionDto;
import org.accounting.system.dtos.authorization.request.RoleRequestDto;
import org.accounting.system.dtos.authorization.response.RoleResponseDto;
import org.accounting.system.dtos.authorization.update.UpdateRoleRequestDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.authorization.AccessPermission;
import org.accounting.system.entities.authorization.CollectionPermission;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.RoleMapper;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

@ApplicationScoped
public class RoleService {

    @Inject
    RoleRepository roleRepository;

    @Inject
    RequestUserContext requestInformation;

    /**
     * Maps the {@link RoleRequestDto} to {@link Role}.
     * Then the {@link Role} is stored in the mongo database.
     *
     * @param request The POST request body
     * @return The stored role has been turned into a response body
     */
    public RoleResponseDto save(RoleRequestDto request) {

        var role = RoleMapper.INSTANCE.requestToRole(request);

        try{
            roleRepository.save(role);
        } catch (MongoWriteException e) {
            throw new ConflictException("There is already a role with this name : " + request.name);
        }

        return RoleMapper.INSTANCE.roleToResponse(role);
    }

    /**
     * It examines whether the role has access to execute an operation to a collection.
     * {@link AccessType#NEVER} always takes precedence over any other access type.
     * @param providedRoles The roles that have been provided by OIDC server
     * @return true or false
     */
    public boolean hasAccess(Set<String> providedRoles, Collection collection, Operation operation){

       List<AccessType> accessTypeList = providedRoles
               .stream()
               .map(role-> roleRepository.getRoleAccessPermissionsUponACollection(role, collection))
               .flatMap(java.util.Collection::stream)
               .filter(permission -> permission.operation.equals(operation))
               .map(permission -> permission.accessType)
               .collect(Collectors.toList());

       AccessType precedence = AccessType.higherPrecedence(accessTypeList);
       requestInformation.setAccessType(precedence);

       return precedence.access;
    }

    /**
     * It examines whether the role has access to execute an operation to a collection.
     * @param providedRoles The roles that have been provided by OIDC server
     * @return true or false
     */
    public boolean hasRoleAccess(Set<String> providedRoles, Collection collection, Operation operation){

        return providedRoles
                .stream()
                .map(role-> roleRepository.getRoleAccessPermissionsUponACollection(role, collection))
                .flatMap(java.util.Collection::stream)
                .anyMatch(permission -> permission.operation.equals(operation));
    }

    /**
     * Returns the Access Type of a specific Operation
     * @param providedRoles The roles that have been provided by OIDC server
     * @return the Access Type
     */
    public AccessType getAccessType(List<String> providedRoles, Collection collection, Operation operation){

        List<AccessType> accessTypeList = providedRoles
                .stream()
                .map(role-> roleRepository.getRoleAccessPermissionsUponACollection(role, collection))
                .flatMap(java.util.Collection::stream)
                .filter(permission -> permission.operation.equals(operation))
                .map(permission -> permission.accessType)
                .collect(Collectors.toList());

        AccessType precedence = AccessType.higherAccessPrecedence(accessTypeList);

        return precedence;
    }

    public Role checkIfRoleExists(String roleName){

        return roleRepository.getRoleByName(roleName).orElseThrow(()->new NotFoundException("There is no Role with name : "+roleName));
    }

    public Role getRoleByName(String name){

        return checkIfRoleExists(name);
    }

    /**
     * Returns the available Accounting System roles
     * @return role response body
     */
    public List<RoleResponseDto> fetchRoles(){

        var roles = roleRepository.getAllEntities();

        var rolesToResponse = RoleMapper.INSTANCE.rolesToResponse(roles);

        return rolesToResponse;
    }

    /**
     * Returns the N Roles from the given page.
     *
     * @param page Indicates the page number.
     * @param size The number of Roles to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<RoleResponseDto> findAllRolesPageable(int page, int size, UriInfo uriInfo){

        PanacheQuery<Role> panacheQuery = roleRepository.find("system = ?1", false).page(Page.of(page, size));

        return new PageResource<>(panacheQuery, RoleMapper.INSTANCE.rolesToResponse(panacheQuery.list()), uriInfo);
    }

    /**
     * Fetches a Role by given id.
     *
     * @param id The Role id
     * @return The corresponding Role
     */
    public RoleResponseDto fetchRole(String id){

        var role = roleRepository.fetchEntityById(new ObjectId(id));

        return RoleMapper.INSTANCE.roleToResponse(role);
    }

    /**
     * Delete a Role by given id.
     * @param roleId The Role to be deleted
     * @return If the operation is successful or not
     * @throws NotFoundException If the Role doesn't exist
     */
    public boolean delete(String roleId){

        return roleRepository.deleteEntityById(new ObjectId(roleId));
    }

    /**
     * This method is responsible for updating a part or all attributes of existing Role.
     *
     * @param id The Role to be updated.
     * @param request The Role attributes to be updated
     * @return The updated Role
     * @throws NotFoundException If the Role doesn't exist
     */
    public RoleResponseDto update(String id, UpdateRoleRequestDto request){

        Role role = null;

        try{
            role = roleRepository.updateEntity(new ObjectId(id), request);
        } catch (MongoWriteException e){
            throw new ConflictException("The role name should be unique. A Role with that name has already been created.");
        }

        return RoleMapper.INSTANCE.roleToResponse(role);
    }

    public Set<CollectionAccessPermissionDto> mergeRoles(Set<Role> roles){

        var permissions = new ArrayList<CollectionPermission>();

       roles
                .stream()
                .flatMap(role->role.getCollectionsAccessPermissions().stream())
                .collect(groupingBy(collectionPermission -> collectionPermission.collection))
                .forEach((key, value) -> {
                    var accessPermissions = new ArrayList<AccessPermission>();
                    value
                            .stream()
                            .flatMap(collectionPermission -> collectionPermission.accessPermissions.stream())
                            .collect(groupingBy(accessPermission -> accessPermission.operation))
                            .forEach((key1, value1) -> {
                                var toBeAdded= value1.stream().reduce(BinaryOperator.minBy(comparing(accessPermission -> accessPermission.accessType.precedence)));
                                accessPermissions.add(toBeAdded.get());
                            });

                    var collectionPermission = new CollectionPermission();
                    collectionPermission.accessPermissions = accessPermissions;
                    collectionPermission.collection = key;
                    permissions.add(collectionPermission);
                });

       return RoleMapper.INSTANCE.collectionPermissionsToDto(permissions);
    }
}