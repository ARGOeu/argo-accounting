package org.accounting.system.mappers;

import org.accounting.system.beans.RequestInformation;
import org.accounting.system.dtos.acl.role.RoleAccessControlRequestDto;
import org.accounting.system.dtos.acl.role.RoleAccessControlResponseDto;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import javax.enterprise.inject.spi.CDI;

/**
 * This interface is responsible for turning an Access Control Entity into a request/response and vice versa.
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
