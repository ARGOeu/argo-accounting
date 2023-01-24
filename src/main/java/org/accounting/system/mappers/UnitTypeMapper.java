package org.accounting.system.mappers;


import org.accounting.system.beans.RequestInformation;
import org.accounting.system.dtos.unittype.UnitTypeDto;
import org.accounting.system.dtos.unittype.UpdateUnitTypeRequestDto;
import org.accounting.system.entities.UnitType;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import javax.enterprise.inject.spi.CDI;
import java.util.List;

/**
 * This mapper turns the incoming requests related to UnitType into database entities.
 * Additionally, it converts the database entities to suitable responses.
 */
@Mapper(uses= UtilMapper.class, imports = {StringUtils.class})
public interface UnitTypeMapper {

    UnitTypeMapper INSTANCE = Mappers.getMapper( UnitTypeMapper.class );

    /**
     * It accepts the incoming request and turns it into a database entity.
     * @param request the incoming request for creating a new Unit Type
     * @return the Unit Type entity to be stored in the database
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    UnitType requestToUnitType(UnitTypeDto request);

    /**
     * It accepts the database document and converts it to appropriate response.
     * @param unitType the stored entity in the database
     * @return the converted entity to proper response
     */
    UnitTypeDto unitTypeToResponse(UnitType unitType);

    @Mapping(target = "unit", expression = "java(StringUtils.isNotEmpty(request.unit) ? request.unit : unitType.getUnit())")
    @Mapping(target = "description", expression = "java(StringUtils.isNotEmpty(request.description) ? request.description : unitType.getDescription())")
    void updateUnitTypeFromDto(UpdateUnitTypeRequestDto request, @MappingTarget UnitType unitType);

    List<UnitTypeDto> unitTypesToResponse(List<UnitType> unitTypes);

    @AfterMapping
    default void setCreatorId(UnitTypeDto source, @MappingTarget UnitType unitType) {
        RequestInformation requestInformation = CDI.current().select(RequestInformation.class).get();
        unitType.setCreatorId(requestInformation.getSubjectOfToken());
    }
}
