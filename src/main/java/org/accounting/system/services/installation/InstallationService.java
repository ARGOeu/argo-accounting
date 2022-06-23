package org.accounting.system.services.installation;

import org.accounting.system.beans.RequestInformation;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.installation.UpdateInstallationRequestDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.endpoints.InstallationEndpoint;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.ProjectionQuery;
import org.accounting.system.interceptors.annotations.AccessPermissionsUtil;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.installation.InstallationAccessAlwaysRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.HierarchicalRelationService;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
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
    RequestInformation requestInformation;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    ProviderRepository providerRepository;

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

        accessibilityOfProjectAndProvider(request.project, request.organisation);

        installationAccessAlwaysRepository.exist(request.infrastructure, request.installation);

        var installation = installationAccessAlwaysRepository.save(request);

        return fetchInstallation(installation.getId().toString());
    }

    /**
     * Delete an Installation by given id.
     * @param installationId The Metric Definition to be deleted.
     * @return If the operation is successful or not.
     */
    public boolean delete(String installationId){

        var installation = installationAccessAlwaysRepository.findById(new ObjectId(installationId));

        accessibilityOfProjectAndProvider(installation.getProject(), installation.getOrganisation());

        return installationAccessAlwaysRepository.deleteEntityById(new ObjectId(installationId));
    }

    /**
     * Fetches an Installation by given id.
     *
     * @param id The Installation id.
     * @return The corresponding Installation.
     */
    public InstallationResponseDto fetchInstallation(String id){

        var installation = installationAccessAlwaysRepository.findById(new ObjectId(id));

        accessibilityOfProjectAndProvider(installation.getProject(), installation.getOrganisation());

        var projection = installationRepository.lookUpEntityById("MetricDefinition", "unit_of_access", "_id", "unit_of_access", InstallationProjection.class, new ObjectId(id));

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

        Installation installation = installationAccessAlwaysRepository.findById(new ObjectId(id));

        accessibilityOfProjectAndProvider(installation.getProject(), installation.getOrganisation());

        InstallationMapper.INSTANCE.updateInstallationFromDto(request, installation);

        if(!StringUtils.isAllBlank(installation.getInstallation(), installation.getInfrastructure())){
            installationAccessAlwaysRepository.exist(installation.getInfrastructure(), installation.getInstallation());
        }

        installationAccessAlwaysRepository.updateEntity(installation, new ObjectId(id));

        return fetchInstallation(id);
    }

    /**
     * Returns the N Installations from the given page.
     *
     * @param page Indicates the page number.
     * @param size The number of Installations to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<InstallationProjection, InstallationResponseDto> findAllInstallationsPageable(int page, int size, UriInfo uriInfo){

        ProjectionQuery<InstallationProjection> projectionQuery = null;

        int count = 0;

        for(AccessPermissionsUtil util: requestInformation.getAccessPermissions()) {

            requestInformation.setAccessType(util.getAccessType());

            try{
                switch (util.getCollection()){
                    case Project:
                        projectionQuery = projectRepository.lookupInstallations("MetricDefinition", "unit_of_access", "_id", "unit_of_access", page, size, InstallationProjection.class);
                        break;
                    case Provider:
                        projectionQuery = providerRepository.lookupInstallations("MetricDefinition", "unit_of_access", "_id", "unit_of_access", page, size, InstallationProjection.class);
                        break;
                    default:
                        throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
                }
            } catch (ForbiddenException e){
                count++;
            }

            if(projectionQuery != null){
                break;
            }
        }

        if(count == requestInformation.getAccessPermissions().size()){
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }

        return new PageResource<>(projectionQuery, InstallationMapper.INSTANCE.installationProjectionsToResponse(projectionQuery.list), uriInfo);
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

        var installation = installationAccessAlwaysRepository.findById(new ObjectId(installationId));

        accessibilityOfProjectAndProvider(installation.getProject(), installation.getOrganisation());

        var metric = hierarchicalRelationService.assignMetric(installationId, request);

        return MetricMapper.INSTANCE.metricToResponse(metric);
    }

    private void accessibilityOfProjectAndProvider(String project, String provider){

        Boolean accessibility = null;

        int count = 0;

        for(AccessPermissionsUtil util: requestInformation.getAccessPermissions()) {

            requestInformation.setAccessType(util.getAccessType());

            try{
                switch (util.getCollection()) {
                    case Project:
                        accessibility = projectRepository.accessibility(project);
                        break;
                    case Provider:
                        accessibility = providerRepository.accessibility(project, provider);
                        break;
                    default:
                        throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
                }
            } catch (ForbiddenException e){
                count++;
            }

            if(accessibility != null){
                break;
            }
        }

        if(count == requestInformation.getAccessPermissions().size()){
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }
}
