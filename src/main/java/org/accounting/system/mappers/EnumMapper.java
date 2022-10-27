package org.accounting.system.mappers;

import org.accounting.system.dtos.enums.EnumResponseDto;
import org.accounting.system.enums.Operand;
import org.accounting.system.enums.Operator;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * This interface is responsible for turning a Enum Entity into a request/response and vice versa.
 */
@Mapper(imports = StringUtils.class)
public interface EnumMapper {

    EnumMapper INSTANCE = Mappers.getMapper( EnumMapper.class );


    List<EnumResponseDto> operandsToResponse(List<Operand> operands);

    List<EnumResponseDto> operatorsToResponse(List<Operator> operators);

//    Enum requestToEnum(ProviderRequestDto request);

    //ProviderResponseDto providerToResponse(Provider provider);


    //}
}