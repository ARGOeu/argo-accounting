package org.accounting.system.mappers;

import jakarta.enterprise.inject.spi.CDI;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.dtos.acl.role.RoleAccessControlRequestDto;
import org.accounting.system.dtos.acl.role.RoleAccessControlResponseDto;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.authorization.Role;
import org.mapstruct.AfterMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This mapper turns the incoming requests, expressed as Data Transform Objects and related to creating or updating a {@link RoleAccessControl RoleAccessControl} into database entities.
 * Additionally, it converts the database entities to suitable responses.
 * To be more accurate, to suitable Data Transform Objects.
 */
@Mapper
public interface AccessControlMapper {


    AccessControlMapper INSTANCE = Mappers.getMapper( AccessControlMapper.class );

    @Mapping(target = "roles", ignore = true)
    RoleAccessControl requestToRoleAccessControl(RoleAccessControlRequestDto request);

    @Named("aclToDto")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToDto")
    RoleAccessControlResponseDto roleAccessControlToResponse(RoleAccessControl response);

    @IterableMapping(qualifiedByName = "aclToDto")
    List<RoleAccessControlResponseDto> roleAccessControlToResponse(List<RoleAccessControl> permissions);

    @AfterMapping
    default void setCreatorId(RoleAccessControlRequestDto source, @MappingTarget RoleAccessControl accessControl) {
        var requestInformation = CDI.current().select(RequestUserContext.class).get();
        accessControl.setCreatorId(requestInformation.getId());
    }

    @Named("rolesToDto")
    default Set<String> rolesToDto(Set<Role> roles) {

        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }
}
