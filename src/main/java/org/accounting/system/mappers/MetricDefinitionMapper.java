package org.accounting.system.mappers;


import org.accounting.system.beans.RequestInformation;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionRequestDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.metricdefinition.UpdateMetricDefinitionRequestDto;
import org.accounting.system.entities.MetricDefinition;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import javax.enterprise.inject.spi.CDI;
import java.util.List;


@Mapper(imports = StringUtils.class)
public interface MetricDefinitionMapper {

    MetricDefinitionMapper INSTANCE = Mappers.getMapper( MetricDefinitionMapper.class );

    MetricDefinition requestToMetricDefinition(MetricDefinitionRequestDto request);

    @Mapping( target="id", expression="java(metricDefinition.getId().toString())")
    MetricDefinitionResponseDto metricDefinitionToResponse(MetricDefinition metricDefinition);

    @Mapping( target="id", expression="java(metricDefinition.getId().toString())")
    List<MetricDefinitionResponseDto> metricDefinitionsToResponse(List<MetricDefinition> metricDefinitions);

    @Mapping(target = "metricName", expression = "java(StringUtils.isNotEmpty(request.metricName) ? request.metricName : metricDefinition.getMetricName())")
    @Mapping(target = "metricDescription", expression = "java(StringUtils.isNotEmpty(request.metricDescription) ? request.metricDescription : metricDefinition.getMetricDescription())")
    @Mapping(target = "unitType", expression = "java(StringUtils.isNotEmpty(request.unitType) ? request.unitType : metricDefinition.getUnitType())")
    @Mapping(target = "metricType", expression = "java(StringUtils.isNotEmpty(request.metricType) ? request.metricType : metricDefinition.getMetricType())")
    void updateMetricDefinitionFromDto(UpdateMetricDefinitionRequestDto request, @MappingTarget MetricDefinition metricDefinition);

    @AfterMapping
    default void setCreatorId(MetricDefinitionRequestDto source, @MappingTarget MetricDefinition metricDefinition) {
        RequestInformation requestInformation = CDI.current().select(RequestInformation.class).get();
        metricDefinition.setCreatorId(requestInformation.getSubjectOfToken());
    }
}
