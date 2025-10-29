package org.accounting.system.mappers;

import org.accounting.system.dtos.ActorEntitlementsDto;
import org.accounting.system.entities.actor.ActorEntitlements;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(imports = StringUtils.class)
public interface ActorEntitlementsMapper {

    ActorEntitlementsMapper INSTANCE = Mappers.getMapper( ActorEntitlementsMapper.class );

    @Named("actorEntitlementToDto")
    ActorEntitlementsDto actorEntitlementsToDto(ActorEntitlements actorEntitlements);

    @IterableMapping(qualifiedByName="actorEntitlementToDto")
    List<ActorEntitlementsDto> actorsEntitlementsToDto(List<ActorEntitlements> actorsEntitlements);
}