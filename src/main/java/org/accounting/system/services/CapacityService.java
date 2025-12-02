package org.accounting.system.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.CapacityDto;
import org.accounting.system.dtos.CapacityRequest;
import org.accounting.system.dtos.UpdateCapacityDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.Capacity;
import org.accounting.system.mappers.CapacityMapper;
import org.accounting.system.repositories.CapacityRepository;
import org.accounting.system.util.Utility;
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

            capacity.setRegisteredOn(Utility.stringToInstant(request.registeredOn));

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

            capacity.setRegisteredOn(Utility.stringToInstant(updateCapacityDto.registeredOn));
        }

        if(!Objects.isNull(updateCapacityDto.value)){

            capacity.setValue(updateCapacityDto.value);
        }

        capacityRepository.update(capacity);

        return CapacityMapper.INSTANCE.capacityToDto(capacity);
    }
}