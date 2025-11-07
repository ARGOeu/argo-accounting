package org.accounting.system.services;

import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.CapacityDto;
import org.accounting.system.dtos.CapacityRequest;
import org.accounting.system.dtos.UpdateCapacityDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.Capacity;
import org.accounting.system.exceptions.CustomValidationException;
import org.accounting.system.mappers.CapacityMapper;
import org.accounting.system.repositories.CapacityRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.Objects;

@ApplicationScoped
public class CapacityService {

    @Inject
    CapacityRepository capacityRepository;

    public void register(String installationId, CapacityRequest request){

        var capacity = new Capacity();

        if(StringUtils.isNotEmpty(request.registeredOn)){

            try{
                var registeredOn = Instant.parse(request.registeredOn);
                capacity.setRegisteredOn(registeredOn);
            } catch (Exception e){
                throw new CustomValidationException("registered_on must be a valid zulu timestamp. found: "+request.registeredOn, HttpResponseStatus.BAD_REQUEST);
            }
        } else {

            capacity.setRegisteredOn(Instant.now());
        }

        capacity.setId(new ObjectId().toString());
        capacity.setValue(request.value);
        capacity.setInstallationId(installationId);
        capacity.setMetricDefinitionId(request.metricDefinitionId);

        capacityRepository.save(capacity);
    }

    public PageResource<CapacityDto> findAll(String installationId, int page, int size, UriInfo uriInfo){

        var panacheQuery = capacityRepository.findInstallationCapacities(installationId, page, size);

        return new PageResource<>(panacheQuery, CapacityMapper.INSTANCE.capacitiesToListDto(panacheQuery.list()), uriInfo);
    }

    public CapacityDto update(String capacityId, UpdateCapacityDto updateCapacityDto){

        var capacity  = capacityRepository.findById(capacityId);

        if(StringUtils.isNotEmpty(updateCapacityDto.registeredOn)){

            try{
                var registeredOn = Instant.parse(updateCapacityDto.registeredOn);
                capacity.setRegisteredOn(registeredOn);
            } catch (Exception e){
                throw new CustomValidationException("registered_on must be a valid zulu timestamp. found: "+updateCapacityDto.registeredOn, HttpResponseStatus.BAD_REQUEST);
            }
        }

        if(!Objects.isNull(updateCapacityDto.value)){

            capacity.setValue(updateCapacityDto.value);
        }

        capacityRepository.update(capacity);

        return CapacityMapper.INSTANCE.capacityToDto(capacity);
    }
}