package org.accounting.system.mappers;

import org.accounting.system.dtos.client.ClientResponseDto;
import org.accounting.system.entities.client.Client;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * This interface is responsible for turning a Client Entity into a request/response and vice versa.
 */
@Mapper(imports = StringUtils.class)
public interface ClientMapper {

    ClientMapper INSTANCE = Mappers.getMapper( ClientMapper.class );

    ClientResponseDto clientToResponse(Client client);

    List<ClientResponseDto> clientsToResponse(List<Client> clients);
}