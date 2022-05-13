package org.accounting.system.mappers;

import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.metric.UpdateMetricRequestDto;
import org.accounting.system.entities.Metric;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.Objects;
import java.util.List;

/**
 * This interface is responsible for turning a Metric Entity into a request/response and vice versa.
 */
@Mapper(imports = {Instant.class, StringUtils.class, Objects.class})
public interface MetricMapper {

    MetricMapper INSTANCE = Mappers.getMapper( MetricMapper.class );

    @Mapping( target="start", expression="java(Instant.parse(request.start))")
    @Mapping( target="end", expression="java(Instant.parse(request.end))")
    Metric requestToMetric(MetricRequestDto request);

    @Mapping( target="id", expression="java(metric.getId().toString())")
    MetricResponseDto metricToResponse(Metric metric);

    @Mapping(target = "metricDefinitionId", expression = "java(StringUtils.isNotEmpty(request.metricDefinitionId) ? request.metricDefinitionId : metric.getMetricDefinitionId())")
    @Mapping(target = "resourceId", expression = "java(StringUtils.isNotEmpty(request.resourceId) ? request.resourceId : metric.getResourceId())")
    @Mapping(target = "start", expression = "java(StringUtils.isNotEmpty(request.start) ? Instant.parse(request.start) : metric.getStart())")
    @Mapping(target = "end", expression = "java(StringUtils.isNotEmpty(request.end) ? Instant.parse(request.end) : metric.getEnd())")
    @Mapping(target = "value", expression = "java(Objects.nonNull(request.value) ? request.value : metric.getValue())")
    void updateMetricFromDto(UpdateMetricRequestDto request, @MappingTarget Metric metric);

    @Mapping( target="id", expression="java(metric.getId().toString())")
    List<MetricResponseDto> metricsToResponse(List<Metric> metrics);
}
