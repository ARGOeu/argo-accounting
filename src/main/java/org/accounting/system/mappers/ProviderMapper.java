package org.accounting.system.mappers;

import org.accounting.system.clients.responses.eoscportal.EOSCProvider;
import org.accounting.system.dtos.provider.ProviderResponseDto;
import org.accounting.system.entities.Provider;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * This interface is responsible for turning a Provider Entity into a request/response and vice versa.
 */
@Mapper
public interface ProviderMapper {

    ProviderMapper INSTANCE = Mappers.getMapper( ProviderMapper.class );

    List<Provider> eoscProvidersToProviders(List<EOSCProvider> providers);

    List<ProviderResponseDto> providersToResponse(List<Provider> providers);
}