package org.accounting.system.mappers;

import jakarta.enterprise.inject.spi.CDI;
import org.accounting.system.beans.RequestInformation;
import org.accounting.system.clients.responses.eoscportal.EOSCProvider;
import org.accounting.system.dtos.provider.ProviderRequestDto;
import org.accounting.system.dtos.provider.ProviderResponseDto;
import org.accounting.system.dtos.provider.UpdateProviderRequestDto;
import org.accounting.system.entities.provider.Provider;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * This mapper turns the incoming requests, expressed as Data Transform Objects and related to creating or updating a {@link Provider Provider} into database entities.
 * Additionally, it converts the database entities to suitable responses.
 * To be more accurate, to suitable Data Transform Objects.
 */
@Mapper(imports = StringUtils.class)
public interface ProviderMapper {

    ProviderMapper INSTANCE = Mappers.getMapper( ProviderMapper.class );

    List<Provider> eoscProvidersToProviders(List<EOSCProvider> providers);

    List<ProviderResponseDto> providersToResponse(List<Provider> providers);

    Provider requestToProvider(ProviderRequestDto request);

    @Mapping(target = "creatorId", expression = "java(StringUtils.isNotEmpty(provider.getCreatorId()) ? provider.getCreatorId() : StringUtils.EMPTY)")
    ProviderResponseDto providerToResponse(Provider provider);

    @Mapping(target = "id", expression = "java(StringUtils.isNotEmpty(request.id) ? request.id : provider.getId())")
    @Mapping(target = "name", expression = "java(StringUtils.isNotEmpty(request.name) ? request.name : provider.getName())")
    @Mapping(target = "website", expression = "java(StringUtils.isNotEmpty(request.website) ? request.website : provider.getWebsite())")
    @Mapping(target = "abbreviation", expression = "java(StringUtils.isNotEmpty(request.abbreviation) ? request.abbreviation : provider.getAbbreviation())")
    @Mapping(target = "logo", expression = "java(StringUtils.isNotEmpty(request.logo) ? request.logo : provider.getLogo())")
    void updateProviderFromDto(UpdateProviderRequestDto request, @MappingTarget Provider provider);

    @AfterMapping
    default void setCreatorId(EOSCProvider source, @MappingTarget Provider provider) {
        provider.setCreatorId(null);
    }

    @AfterMapping
    default void setCreatorId(ProviderRequestDto source, @MappingTarget Provider provider) {
        RequestInformation requestInformation = CDI.current().select(RequestInformation.class).get();
        provider.setCreatorId(requestInformation.getSubjectOfToken());
    }
}