package org.accounting.system.mappers;

import org.accounting.system.beans.RequestInformation;
import org.accounting.system.dtos.acl.role.RoleAccessControlRequestDto;
import org.accounting.system.dtos.acl.role.RoleAccessControlResponseDto;
import org.accounting.system.dtos.acl.role.RoleAccessControlUpdateDto;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import javax.enterprise.inject.spi.CDI;
import java.util.List;

/**
 * This interface is responsible for turning an Access Control Entity into a request/response and vice versa.
 */
@Mapper(uses = UtilMapper.class)
public interface AccessControlMapper {


    AccessControlMapper INSTANCE = Mappers.getMapper( AccessControlMapper.class );

    RoleAccessControl requestToRoleAccessControl(RoleAccessControlRequestDto request);

    RoleAccessControlResponseDto roleAccessControlToResponse(RoleAccessControl response);

    List<RoleAccessControlResponseDto> roleAccessControlsToResponse(List<RoleAccessControl> response);

    @Mapping(target = "who", ignore = true)
    @Mapping(target = "collection", ignore = true)
    @Mapping(target = "entity", ignore = true)
    void updateRoleAccessControlFromDto(RoleAccessControlUpdateDto dto, @MappingTarget RoleAccessControl accessControl);


    @AfterMapping
    default void setCreatorId(RoleAccessControlRequestDto source, @MappingTarget RoleAccessControl accessControl) {
        RequestInformation requestInformation = CDI.current().select(RequestInformation.class).get();
        accessControl.setCreatorId(requestInformation.getSubjectOfToken());
    }
}
