package org.accounting.system.mappers;


import org.accounting.system.dtos.MetricRegistrationDtoRequest;
import org.accounting.system.dtos.MetricRegistrationDtoResponse;
import org.accounting.system.entities.MetricRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MetricRegistrationMapper {

    MetricRegistrationMapper INSTANCE = Mappers.getMapper( MetricRegistrationMapper.class );

    MetricRegistration requestToMetricRegistration(MetricRegistrationDtoRequest request);

    @Mapping( target="id", expression="java(metricRegistration.getId().toString())")
    MetricRegistrationDtoResponse metricRegistrationToResponse(MetricRegistration metricRegistration);

    @Mapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, source = "metricName", target = "metricName")
    @Mapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, source = "metricDescription", target = "metricDescription")
    @Mapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, source = "unitType", target = "unitType")
    @Mapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, source = "metricType", target = "metricType")
    void updateMetricRegistrationFromDto(MetricRegistrationDtoRequest request, @MappingTarget MetricRegistration metricRegistration);


}
