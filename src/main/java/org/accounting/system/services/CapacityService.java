package org.accounting.system.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.CapacityDto;
import org.accounting.system.dtos.CapacityRequest;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.Capacity;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.CapacityMapper;
import org.accounting.system.repositories.CapacityRepository;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@ApplicationScoped
public class CapacityService {

    @Inject
    CapacityRepository capacityRepository;


    public void register(String installationId, CapacityRequest request){

        capacityRepository.findByInstallationAndMetricDefinition(installationId, request.metricDefinitionId).ifPresent(u -> {
            throw new ConflictException("Capacity already exists.");
        });

        var capacity = new Capacity();
        capacity.setId(new ObjectId().toString());
        capacity.setValue(request.value);
        capacity.setInstallationId(installationId);
        capacity.setMetricDefinitionId(request.metricDefinitionId);
        capacity.setRegisteredOn(LocalDateTime.now());

        capacityRepository.save(capacity);
    }

    public PageResource<CapacityDto> findAll(String installationId, int page, int size, UriInfo uriInfo){

        var panacheQuery = capacityRepository.findInstallationCapacities(installationId, page, size);

        return new PageResource<>(panacheQuery, CapacityMapper.INSTANCE.capacitiesToListDto(panacheQuery.list()), uriInfo);
    }
}
