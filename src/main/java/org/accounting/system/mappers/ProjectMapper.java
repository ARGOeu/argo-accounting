package org.accounting.system.mappers;

import org.accounting.system.beans.RequestInformation;
import org.accounting.system.clients.responses.openaire.OpenAireProject;
import org.accounting.system.entities.Project;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import javax.enterprise.inject.spi.CDI;
import java.util.Objects;


/**
 * This mapper turns the incoming requests, expressed as Data Transform Objects and related to creating or updating a {@link Project Project} into database entities.
 * Additionally, it converts the database entities to suitable responses.
 * To be more accurate, to suitable Data Transform Objects.
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

    @AfterMapping
    default void setCreatorId(OpenAireProject source, @MappingTarget Project project) {
        RequestInformation requestInformation = CDI.current().select(RequestInformation.class).get();
        project.setCreatorId(requestInformation.getSubjectOfToken());
    }
}