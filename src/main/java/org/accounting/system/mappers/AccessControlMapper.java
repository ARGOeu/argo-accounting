package org.accounting.system.mappers;

import jakarta.enterprise.inject.spi.CDI;
import org.accounting.system.beans.RequestInformation;
import org.accounting.system.dtos.acl.role.RoleAccessControlRequestDto;
import org.accounting.system.dtos.acl.role.RoleAccessControlResponseDto;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

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

    @Mapping(target = "roles", ignore = true)
    RoleAccessControlResponseDto roleAccessControlToResponse(RoleAccessControl response);

    @AfterMapping
    default void setCreatorId(RoleAccessControlRequestDto source, @MappingTarget RoleAccessControl accessControl) {
        RequestInformation requestInformation = CDI.current().select(RequestInformation.class).get();
        accessControl.setCreatorId(requestInformation.getSubjectOfToken());
    }
}
