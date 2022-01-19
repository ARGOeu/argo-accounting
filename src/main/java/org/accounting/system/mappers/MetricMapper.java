package org.accounting.system.mappers;

import org.accounting.system.dtos.MetricRequestDto;
import org.accounting.system.dtos.MetricResponseDto;
import org.accounting.system.entities.Metric;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

/**
 * This interface is responsible for turning a Metric Entity into a request/response and vice versa.
 */
@Mapper(imports = Instant.class)
public interface MetricMapper {

    MetricMapper INSTANCE = Mappers.getMapper( MetricMapper.class );

    @Mapping( target="start", expression="java(Instant.parse(request.start))")
    @Mapping( target="end", expression="java(Instant.parse(request.end))")
    Metric requestToMetric(MetricRequestDto request);

    @Mapping( target="id", expression="java(metric.getId().toString())")
    MetricResponseDto metricToResponse(Metric metric);
}
