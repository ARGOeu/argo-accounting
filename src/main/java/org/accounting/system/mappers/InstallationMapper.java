package org.accounting.system.mappers;

import org.accounting.system.beans.RequestInformation;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.installation.UpdateInstallationRequestDto;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.InstallationProjection;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import javax.enterprise.inject.spi.CDI;
import java.util.List;

/**
 * This mapper turns the incoming requests, expressed as Data Transform Objects and related to creating or updating a {@link Installation Installation} into database entities.
 * Additionally, it converts the database entities to suitable responses.
 * To be more accurate, to suitable Data Transform Objects.
 */
@Mapper(
        uses= {UtilMapper.class, MetricDefinitionMapper.class},
        imports = {StringUtils.class, ObjectId.class} )
public interface InstallationMapper {

    InstallationMapper INSTANCE = Mappers.getMapper( InstallationMapper.class );

    @Mapping(target="unitOfAccess", expression="java(new ObjectId(request.unitOfAccess))")
    Installation requestToInstallation(InstallationRequestDto request);

    @Mapping( target="id", expression="java(installation.getId().toString())")
    List<InstallationResponseDto> installationProjectionsToResponse(List<InstallationProjection> installations);

    //@Mapping( target="id", expression="java(installation.getId().toString())")
    InstallationResponseDto installationProjectionToResponse(InstallationProjection installation);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "organisation", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "resource", expression = "java(StringUtils.isNotEmpty(request.resource) ? request.resource : installation.getResource())")
    @Mapping(target = "infrastructure", expression = "java(StringUtils.isNotEmpty(request.infrastructure) ? request.infrastructure : installation.getInfrastructure())")
    @Mapping(target = "installation", expression = "java(StringUtils.isNotEmpty(request.installation) ? request.installation : installation.getInstallation())")
    @Mapping(target = "unitOfAccess", expression = "java(StringUtils.isNotEmpty(request.unitOfAccess) ? new ObjectId(request.unitOfAccess) : installation.getUnitOfAccess())")
    void updateInstallationFromDto(UpdateInstallationRequestDto request, @MappingTarget Installation installation);

    @AfterMapping
    default void setCreatorId(InstallationRequestDto source, @MappingTarget Installation installation) {
        RequestInformation requestInformation = CDI.current().select(RequestInformation.class).get();
        installation.setCreatorId(requestInformation.getSubjectOfToken());
    }
}
