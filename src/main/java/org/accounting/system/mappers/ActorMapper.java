package org.accounting.system.mappers;

import org.accounting.system.dtos.ActorDto;
import org.accounting.system.entities.actor.Actor;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(imports = StringUtils.class)
public interface ActorMapper {

    ActorMapper INSTANCE = Mappers.getMapper( ActorMapper.class );

    @Named("actorToDto")
    ActorDto actorToDto(Actor actor);

    @IterableMapping(qualifiedByName="actorToDto")
    List<ActorDto> actorsToListDto(List<Actor> actors);
}