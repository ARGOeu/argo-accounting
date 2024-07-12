package org.accounting.system.mappers;

import jakarta.enterprise.inject.spi.CDI;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.metric.UpdateMetricRequestDto;
import org.accounting.system.entities.Metric;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * This mapper turns the incoming requests, expressed as Data Transform Objects and related to creating or updating a {@link Metric Metric} into database entities.
 * Additionally, it converts the database entities to suitable responses.
 * To be more accurate, to suitable Data Transform Objects.
 */
@Mapper(imports = {Instant.class, StringUtils.class, Objects.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MetricMapper {

    MetricMapper INSTANCE = Mappers.getMapper( MetricMapper.class );

    @Mapping(target="start", expression="java(Instant.parse(request.start))")
    @Mapping(target="end", expression="java(Instant.parse(request.end))")
    Metric requestToMetric(MetricRequestDto request);

    @Named("metricToDto")
    @Mapping(target="id", expression="java(metric.getId().toString())")
    MetricResponseDto metricToResponse(Metric metric);

    @Mapping(target = "metricDefinitionId", expression = "java(StringUtils.isNotEmpty(request.metricDefinitionId) ? request.metricDefinitionId : metric.getMetricDefinitionId())")
    @Mapping(target = "start", expression = "java(StringUtils.isNotEmpty(request.start) ? Instant.parse(request.start) : metric.getStart())")
    @Mapping(target = "end", expression = "java(StringUtils.isNotEmpty(request.end) ? Instant.parse(request.end) : metric.getEnd())")
    @Mapping(target = "value", expression = "java(Objects.nonNull(request.value) ? request.value : metric.getValue())")
    @Mapping(target = "userId", expression = "java(Objects.nonNull(request.userId) ? request.userId : metric.getUserId())")
    @Mapping(target = "groupId", expression = "java(Objects.nonNull(request.groupId) ? request.groupId : metric.getGroupId())")
    void updateMetricFromDto(UpdateMetricRequestDto request, @MappingTarget Metric metric);

    @IterableMapping(qualifiedByName = "metricToDto")
    List<MetricResponseDto> metricsToResponse(List<Metric> metrics);

    @AfterMapping
    default void setCreatorId(MetricRequestDto source, @MappingTarget Metric metric) {
        var requestInformation = CDI.current().select(RequestUserContext.class).get();
        metric.setCreatorId(requestInformation.getId());
    }
}
