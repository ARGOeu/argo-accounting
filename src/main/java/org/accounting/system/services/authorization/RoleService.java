package org.accounting.system.services.authorization;

import com.mongodb.MongoWriteException;
import org.accounting.system.beans.RequestInformation;
import org.accounting.system.dtos.RoleResponseDto;
import org.accounting.system.dtos.authorization.RoleRequestDto;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.enums.AccessType;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.RoleMapper;
import org.accounting.system.repositories.authorization.RoleRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RoleService {

    @Inject
    RoleRepository roleRepository;

    @Inject
    RequestInformation requestInformation;

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
            roleRepository.persist(role);
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
    public boolean hasAccess(List<String> providedRoles, Collection collection, Operation operation){

       List<AccessType> accessTypeList = providedRoles
               .stream()
               .map(role-> roleRepository.getRolePermissionsUponACollection(role, collection))
               .flatMap(java.util.Collection::stream)
               .filter(permission -> permission.operation.equals(operation))
               .map(permission -> permission.accessType)
               .collect(Collectors.toList());

       AccessType precedence = AccessType.higherPrecedence(accessTypeList);
       requestInformation.setAccessType(precedence);

       return precedence.access;
    }

    /**
     * Returns the available Accounting System roles
     * @return role response body
     */
    public List<RoleResponseDto> fetchRoles(){

        var roles = roleRepository.fetchRoles();

        var rolesToResponse = RoleMapper.INSTANCE.rolesToResponse(roles);

        return rolesToResponse;
    }
}