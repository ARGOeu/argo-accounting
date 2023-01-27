package org.accounting.system.mappers;

import org.accounting.system.dtos.enums.EnumResponseDto;
import org.accounting.system.enums.Operand;
import org.accounting.system.enums.Operator;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * This Mapper converts the available {@link Operand operands} and {@link Operator} operators, to suitable responses.
 * Both of them are used in Searching Process.
 */
@Mapper(imports = StringUtils.class)
public interface EnumMapper {

    EnumMapper INSTANCE = Mappers.getMapper( EnumMapper.class );


    List<EnumResponseDto> operandsToResponse(List<Operand> operands);

    List<EnumResponseDto> operatorsToResponse(List<Operator> operators);
}