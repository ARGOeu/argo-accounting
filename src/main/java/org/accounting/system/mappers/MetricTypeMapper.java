package org.accounting.system.mappers;


import org.accounting.system.beans.RequestInformation;
import org.accounting.system.dtos.metrictype.MetricTypeDto;
import org.accounting.system.dtos.metrictype.UpdateMetricTypeRequestDto;
import org.accounting.system.entities.MetricType;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import javax.enterprise.inject.spi.CDI;
import java.util.List;

/**
 * This mapper turns the incoming requests, expressed as Data Transform Objects and related to creating or updating a {@link MetricType Metric Type} into database entities.
 * Additionally, it converts the database entities to suitable responses.
 * To be more accurate, to suitable Data Transform Objects.
 */
@Mapper(uses= UtilMapper.class, imports = {StringUtils.class})
public interface MetricTypeMapper {

    MetricTypeMapper INSTANCE = Mappers.getMapper( MetricTypeMapper.class );

    /**
     * It accepts the incoming request and turns it into a database entity.
     * @param request the incoming request for creating a new Metric Type
     * @return the Metric Type entity to be stored in the database
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    MetricType requestToMetricType(MetricTypeDto request);

    /**
     * It accepts the database document and converts it to appropriate response.
     * @param metricType the stored entity in the database
     * @return the converted entity to proper response
     */
    MetricTypeDto metricTypeToResponse(MetricType metricType);

    /**
     * This method is responsible for editing a particular entity's properties.
     * @param request The MetricType attributes that have been requested to be updated.
     * @param metricType The MetricType entity to be updated.
     */
    @Mapping(target = "metricType", expression = "java(StringUtils.isNotEmpty(request.metricType) ? request.metricType : metricType.getMetricType())")
    @Mapping(target = "description", expression = "java(StringUtils.isNotEmpty(request.description) ? request.description : metricType.getDescription())")
    void updateMetricTypeFromDto(UpdateMetricTypeRequestDto request, @MappingTarget MetricType metricType);

    /**
     * This method transforms a list of MetricType entities to appropriate Data Transfer Objects.
     * @param metricTypes A list of MetricType entities
     * @return The given entities transformed to suitable Data Transfer Objects
     */
    List<MetricTypeDto> metricTypesToResponse(List<MetricType> metricTypes);

    @AfterMapping
    default void setCreatorId(MetricTypeDto source, @MappingTarget MetricType metricType) {
        RequestInformation requestInformation = CDI.current().select(RequestInformation.class).get();
        metricType.setCreatorId(requestInformation.getSubjectOfToken());
    }
}
