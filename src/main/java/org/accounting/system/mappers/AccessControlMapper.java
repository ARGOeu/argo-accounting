package org.accounting.system.mappers;

import org.accounting.system.dtos.acl.AccessControlRequestDto;
import org.accounting.system.dtos.acl.AccessControlResponseDto;
import org.accounting.system.dtos.acl.AccessControlUpdateDto;
import org.accounting.system.entities.acl.AccessControl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * This interface is responsible for turning an Access Control Entity into a request/response and vice versa.
 */
@Mapper(uses = UtilMapper.class)
public interface AccessControlMapper {


    AccessControlMapper INSTANCE = Mappers.getMapper( AccessControlMapper.class );

    AccessControl requestToAccessControl(AccessControlRequestDto request);

    AccessControlResponseDto accessControlToResponse(AccessControl response);

    @Mapping(target = "who", ignore = true)
    @Mapping(target = "collection", ignore = true)
    @Mapping(target = "entity", ignore = true)
    void updateAccessControlFromDto(AccessControlUpdateDto dto, @MappingTarget AccessControl accessControl);
}
