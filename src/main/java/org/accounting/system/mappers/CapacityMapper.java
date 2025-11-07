package org.accounting.system.mappers;

import org.accounting.system.dtos.CapacityDto;
import org.accounting.system.entities.Capacity;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(imports = StringUtils.class)
public interface CapacityMapper {

    CapacityMapper INSTANCE = Mappers.getMapper( CapacityMapper.class );

    @Named("capacityToDto")
    CapacityDto capacityToDto(Capacity capacity);

    @IterableMapping(qualifiedByName="capacityToDto")
    List<CapacityDto> capacitiesToListDto(List<Capacity> capacities);
}
