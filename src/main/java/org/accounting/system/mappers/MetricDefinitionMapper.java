package org.accounting.system.mappers;


import org.accounting.system.dtos.MetricDefinitionDtoRequest;
import org.accounting.system.dtos.MetricDefinitionDtoResponse;
import org.accounting.system.dtos.UpdateMetricDefinitionDtoRequest;
import org.accounting.system.entities.MetricDefinition;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(imports = StringUtils.class)
public interface MetricDefinitionMapper {

    MetricDefinitionMapper INSTANCE = Mappers.getMapper( MetricDefinitionMapper.class );

    MetricDefinition requestToMetricDefinition(MetricDefinitionDtoRequest request);

    MetricDefinitionDtoRequest updateRequestToMetricDefinitionDtoRequest(UpdateMetricDefinitionDtoRequest update);

    @Mapping( target="id", expression="java(metricDefinition.getId().toString())")
    MetricDefinitionDtoResponse metricDefinitionToResponse(MetricDefinition metricDefinition);

    @Mapping( target="id", expression="java(metricDefinition.getId().toString())")
    List<MetricDefinitionDtoResponse> metricDefinitionsToResponse(List<MetricDefinition> metricDefinitions);


    @Mapping(target = "metricName", expression = "java(StringUtils.isNotEmpty(request.metricName) ? request.metricName : metricDefinition.getMetricName())")
    @Mapping(target = "metricDescription", expression = "java(StringUtils.isNotEmpty(request.metricDescription) ? request.metricDescription : metricDefinition.getMetricDescription())")
    @Mapping(target = "unitType", expression = "java(StringUtils.isNotEmpty(request.unitType) ? request.unitType : metricDefinition.getUnitType())")
    @Mapping(target = "metricType", expression = "java(StringUtils.isNotEmpty(request.metricType) ? request.metricType : metricDefinition.getMetricType())")
    void updateMetricDefinitionFromDto(UpdateMetricDefinitionDtoRequest request, @MappingTarget MetricDefinition metricDefinition);
}
