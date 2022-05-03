package org.accounting.system.mappers;

import org.accounting.system.clients.responses.openaire.OpenAireProject;
import org.accounting.system.dtos.project.ProjectResponseDto;
import org.accounting.system.entities.Project;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

/**
 * This interface is responsible for turning a Project Entity into a request/response and vice versa.
 */
@Mapper(imports = {StringUtils.class, Objects.class})
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper( ProjectMapper.class );

    @Mapping(target = "id", expression = "java(Objects.nonNull(project.response.results.result.get(project.response.header.total.value-1).metadata.entity.project.code) ? project.response.results.result.get(project.response.header.total.value-1).metadata.entity.project.code.value : StringUtils.EMPTY)")
    @Mapping(target = "acronym", expression = "java(Objects.nonNull(project.response.results.result.get(project.response.header.total.value-1).metadata.entity.project.acronym) ? project.response.results.result.get(project.response.header.total.value-1).metadata.entity.project.acronym.value : StringUtils.EMPTY)")
    @Mapping(target = "title", expression = "java(Objects.nonNull(project.response.results.result.get(project.response.header.total.value-1).metadata.entity.project.title) ? project.response.results.result.get(project.response.header.total.value-1).metadata.entity.project.title.value : StringUtils.EMPTY)")
    @Mapping(target = "startDate", expression = "java(Objects.nonNull(project.response.results.result.get(project.response.header.total.value-1).metadata.entity.project.startDate) ? project.response.results.result.get(project.response.header.total.value-1).metadata.entity.project.startDate.value : StringUtils.EMPTY)")
    @Mapping(target = "endDate", expression = "java(Objects.nonNull(project.response.results.result.get(project.response.header.total.value-1).metadata.entity.project.endDate) ? project.response.results.result.get(project.response.header.total.value-1).metadata.entity.project.endDate.value : StringUtils.EMPTY)")
    @Mapping(target = "callIdentifier", expression = "java(Objects.nonNull(project.response.results.result.get(project.response.header.total.value-1).metadata.entity.project.callIdentifier) ? project.response.results.result.get(project.response.header.total.value-1).metadata.entity.project.callIdentifier.value : StringUtils.EMPTY)")
    Project openAireResponseToProject(OpenAireProject project);

    ProjectResponseDto projectToDto(Project project);
}