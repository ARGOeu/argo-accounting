package org.accounting.system.mappers;

import org.accounting.system.dtos.RoleResponseDto;
import org.accounting.system.entities.authorization.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * This interface is responsible for turning a Role Entity into a request/response and vice versa.
 */
@Mapper(uses= UtilMapper.class)
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper( RoleMapper.class );

    List<RoleResponseDto> rolesToResponse(List<Role> roles);
}