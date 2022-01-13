package org.accounting.system.mappers;


import org.accounting.system.dtos.MetricRegistrationDtoRequest;
import org.accounting.system.dtos.MetricRegistrationDtoResponse;
import org.accounting.system.dtos.UpdateMetricRegistrationDtoRequest;
import org.accounting.system.entities.MetricRegistration;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(imports = StringUtils.class)
public interface MetricRegistrationMapper {

    MetricRegistrationMapper INSTANCE = Mappers.getMapper( MetricRegistrationMapper.class );

    MetricRegistration requestToMetricRegistration(MetricRegistrationDtoRequest request);

    @Mapping( target="id", expression="java(metricRegistration.getId().toString())")
    MetricRegistrationDtoResponse metricRegistrationToResponse(MetricRegistration metricRegistration);

    @Mapping(target = "metricName", expression = "java(StringUtils.isNotEmpty(request.metricName) ? request.metricName : metricRegistration.getMetricName())")
    @Mapping(target = "metricDescription", expression = "java(StringUtils.isNotEmpty(request.metricDescription) ? request.metricDescription : metricRegistration.getMetricDescription())")
    @Mapping(target = "unitType", expression = "java(StringUtils.isNotEmpty(request.unitType) ? request.unitType : metricRegistration.getUnitType())")
    @Mapping(target = "metricType", expression = "java(StringUtils.isNotEmpty(request.metricType) ? request.metricType : metricRegistration.getMetricType())")
    void updateMetricRegistrationFromDto(UpdateMetricRegistrationDtoRequest request, @MappingTarget MetricRegistration metricRegistration);


}
