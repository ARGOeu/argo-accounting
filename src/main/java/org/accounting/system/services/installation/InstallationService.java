package org.accounting.system.services.installation;

import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.installation.UpdateInstallationRequestDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.endpoints.InstallationEndpoint;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.enums.RelationType;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
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
    HierarchicalRelationRepository hierarchicalRelationRepository;

    /**
     * Maps the {@link InstallationRequestDto} to {@link Installation}.
     * Then the {@link Installation} is stored in the mongo database.
     *
     * @param request The POST request body.
     * @return The stored Installation has been turned into a response body.
     */
    public InstallationResponseDto save(InstallationRequestDto request) {

        var installationToBeStored = InstallationMapper.INSTANCE.requestToInstallation(request);

        installationRepository.persist(installationToBeStored);

        HierarchicalRelation project = new HierarchicalRelation(request.project, RelationType.PROJECT);

        HierarchicalRelation provider = new HierarchicalRelation(request.organisation, project, RelationType.PROVIDER);

        HierarchicalRelation installation = new HierarchicalRelation(installationToBeStored.getId().toString(), provider, RelationType.INSTALLATION);

        hierarchicalRelationRepository.save(project, null);
        hierarchicalRelationRepository.save(provider, null);
        hierarchicalRelationRepository.save(installation, null);

        return fetchInstallation(installationToBeStored.getId().toString());
    }

    /**
     * Delete an Installation by given id.
     * @param installationId The Metric Definition to be deleted.
     * @return If the operation is successful or not.
     */
    public boolean delete(String installationId){

        return installationRepository.deleteEntityById(new ObjectId(installationId));
    }

    /**
     * Fetches an Installation by given id.
     *
     * @param id The Installation id.
     * @return The corresponding Installation.
     */
    public InstallationResponseDto fetchInstallation(String id){

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

        installationRepository.updateEntity(id, request);

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

        var projectionQuery = installationRepository.lookup("MetricDefinition", "unit_of_access", "_id", "unit_of_access", page, size, InstallationProjection.class);

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

    public void exist(String infrastructure, String installation){

        installationRepository.exist(infrastructure, installation);
    }

}
