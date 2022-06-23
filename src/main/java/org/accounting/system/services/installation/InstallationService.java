package org.accounting.system.services.installation;

import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.installation.UpdateInstallationRequestDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.endpoints.InstallationEndpoint;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.installation.InstallationAccessAlwaysRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.HierarchicalRelationService;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;

/**
 * This service exposes business logic, which uses the {@link InstallationRepository}.
 * It is used to keep logic to a minimum in {@link InstallationEndpoint} and
 * {@link InstallationRepository}
 */
@ApplicationScoped
public class InstallationService {

    @Inject
    InstallationRepository installationRepository;

    @Inject
    InstallationAccessAlwaysRepository installationAccessAlwaysRepository;

    @Inject
    HierarchicalRelationService hierarchicalRelationService;

    /**
     * Maps the {@link InstallationRequestDto} to {@link Installation}.
     * Then the {@link Installation} is stored in the mongo database.
     *
     * @param request The POST request body.
     * @return The stored Installation has been turned into a response body.
     */
    public InstallationResponseDto save(InstallationRequestDto request) {

        installationRepository.exist(request.infrastructure, request.installation);

        var installation = installationAccessAlwaysRepository.save(request);

        return fetchInstallation(installation.getId().toString());
    }

    /**
     * Delete an Installation by given id.
     * @param installationId The Metric Definition to be deleted.
     * @return If the operation is successful or not.
     */
    public boolean delete(String installationId){

        return installationAccessAlwaysRepository.deleteEntityById(new ObjectId(installationId));
    }

    /**
     * Fetches an Installation by given id.
     *
     * @param id The Installation id.
     * @return The corresponding Installation.
     */
    public InstallationResponseDto fetchInstallation(String id){

        var projection = installationAccessAlwaysRepository.lookUpEntityById("MetricDefinition", "unit_of_access", "_id", "unit_of_access", InstallationProjection.class, new ObjectId(id));

        return InstallationMapper.INSTANCE.installationProjectionToResponse(projection);
    }

    /**
     * This method is responsible for checking if there is a Provider with given name or id.
     * Then, it calls the {@link ProviderRepository providerRepository} to update a Provider.
     *
     * @param id The Installation to be updated.
     * @param request The Installation attributes to be updated.
     * @return The updated Installation.
     */
    public InstallationResponseDto update(String id, UpdateInstallationRequestDto request){

        Installation installation = installationRepository.findById(new ObjectId(id));

        InstallationMapper.INSTANCE.updateInstallationFromDto(request, installation);

        if(!StringUtils.isAllBlank(installation.getInstallation(), installation.getInfrastructure())){
            installationRepository.exist(installation.getInfrastructure(), installation.getInstallation());
        }

        installationAccessAlwaysRepository.updateEntity(installation, new ObjectId(id));

        return fetchInstallation(id);
    }

    /**
     * Checks if the given installation belongs to given provider.
     *
     * @param installationId The installation
     * @param providerId The provider
     * @throws BadRequestException if the installation doesn't belong to the given provider
     */
    public void checkIfInstallationBelongsToProvider(String installationId, String providerId){

        var installation = installationRepository.findById(new ObjectId(installationId));

        if(!installation.getOrganisation().equals(providerId)){
            throw new BadRequestException(String.format("The installation doesn't belong to the following Provider : %s", providerId));
        }
    }

    /**
     * This method is responsible for creating a hierarchical structure which consists of :
     * - Root -> Project
     * - Intermediate Level -> Provider
     * - Leaf -> Installation
     * The Metric is assigned to the given installation (leaf)
     *
     * @param installationId Leaf of structure
     * @param request Metric to be created and assigned to the given installation
     * @return The assigned Metric
     */
    public MetricResponseDto assignMetric(String installationId, MetricRequestDto request) {

        var metric = hierarchicalRelationService.assignMetric(installationId, request);

        return MetricMapper.INSTANCE.metricToResponse(metric);
    }

    public PageResource<MetricProjection, MetricProjection> fetchAllMetrics(String id, int page, int size, UriInfo uriInfo){

        var installation = installationAccessAlwaysRepository.findById(new ObjectId(id));

        var projection = installationRepository.fetchAllMetrics(installation.getProject() + HierarchicalRelation.PATH_SEPARATOR + installation.getOrganisation() + HierarchicalRelation.PATH_SEPARATOR + id, page, size);

        if(projection.count == 0){
            throw new NotFoundException("No metrics added.");
        }

        return new PageResource<>(projection, projection.list, uriInfo);
    }
}
