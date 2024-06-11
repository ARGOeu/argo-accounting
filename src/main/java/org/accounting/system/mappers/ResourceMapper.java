package org.accounting.system.mappers;

import jakarta.enterprise.inject.spi.CDI;
import org.accounting.system.beans.RequestInformation;
import org.accounting.system.clients.responses.eoscportal.EOSCResource;
import org.accounting.system.dtos.resource.ResourceRequest;
import org.accounting.system.dtos.resource.ResourceResponse;
import org.accounting.system.entities.Resource;
import org.mapstruct.AfterMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * This mapper turns the incoming requests, expressed as Data Transform Objects and related to creating or updating a {@link org.accounting.system.entities.Resource resource} into database entities.
 * Additionally, it converts the database entities to suitable responses.
 * To be more accurate, to suitable Data Transform Objects.
 */
@Mapper
public interface ResourceMapper {

    ResourceMapper INSTANCE = Mappers.getMapper(ResourceMapper.class);

    List<ResourceResponse> eoscResourcesToEoscResourcesDto(List<EOSCResource> resources);

    @Named("resourceToDto")
    ResourceResponse resourceToDto(Resource resource);
    Resource dtoToResource(ResourceRequest request);

    @IterableMapping(qualifiedByName = "resourceToDto")
    List<ResourceResponse> resourcesToResponse(List<Resource> resources);

    @AfterMapping
    default void setCreatorId(ResourceRequest source, @MappingTarget Resource resource) {
        var requestInformation = CDI.current().select(RequestInformation.class).get();
        resource.setCreatorId(requestInformation.getSubjectOfToken());
    }
}