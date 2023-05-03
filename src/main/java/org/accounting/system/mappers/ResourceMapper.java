package org.accounting.system.mappers;

import org.accounting.system.clients.responses.eoscportal.EOSCResource;
import org.accounting.system.dtos.resource.EoscResourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * This mapper turns the incoming requests, expressed as Data Transform Objects and related to creating or updating a {@link Resource resource} into database entities.
 * Additionally, it converts the database entities to suitable responses.
 * To be more accurate, to suitable Data Transform Objects.
 */
@Mapper
public interface ResourceMapper {

    ResourceMapper INSTANCE = Mappers.getMapper(ResourceMapper.class);

    List<EoscResourceDto> eoscResourcesToEoscResourcesDto(List<EOSCResource> resources);
}