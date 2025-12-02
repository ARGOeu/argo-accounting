package org.accounting.system.mappers;


import org.accounting.system.dtos.entitlement.EntitlementDto;
import org.accounting.system.entities.actor.Entitlement;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(imports = StringUtils.class)
public interface EntitlementMapper {

    EntitlementMapper INSTANCE = Mappers.getMapper( EntitlementMapper.class );

    @Named("entitlementToDto")
    EntitlementDto entitlementToDto(Entitlement entitlement);

    @IterableMapping(qualifiedByName="entitlementToDto")
    List<EntitlementDto> entitlementsToListDto(List<Entitlement> entitlements);
}
