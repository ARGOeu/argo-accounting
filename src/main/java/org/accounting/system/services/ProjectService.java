package org.accounting.system.services;

import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.acl.AccessControlRequestDto;
import org.accounting.system.dtos.acl.AccessControlResponseDto;
import org.accounting.system.dtos.acl.AccessControlUpdateDto;
import org.accounting.system.dtos.project.ProjectResponseDto;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.enums.Collection;
import org.accounting.system.mappers.AccessControlMapper;
import org.accounting.system.mappers.ProjectMapper;
import org.accounting.system.repositories.acl.AccessControlRepository;
import org.accounting.system.repositories.project.ProjectRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ProjectService {

    @Inject
    ProjectRepository projectRepository;

    @Inject
    AccessControlRepository accessControlRepository;

    /**
     * An http call is made to Open Aire to retrieve the corresponding Project.
     * It is then stored in the database, converted into a response body and returned.
     *
     * @param id The Project id
     * @return The Project as it is stored in the Accounting system database.
     */
    public ProjectResponseDto save(String id){

        var project = projectRepository.save(id);

        return ProjectMapper.INSTANCE.projectToDto(project);
    }

    /**
     * This method correlates the given Providers with a specific Project and creates an hierarchical structure with root
     * the given Project and children the given Providers.
     *
     * @param projectId The Project id with which the Providers are going to be correlated.
     * @param providerIds List of Providers which will be correlated with a specific Provider
     * @throws NotFoundException If a Provider doesn't exist
     */
    public void associateProjectWithProviders(String projectId, Set<String> providerIds){

        projectRepository.associateProjectWithProviders(projectId, providerIds);
    }

    /**
     * This method dissociated Providers from a specific Project.
     *
     * @param projectId The Project id from which the Providers are going to be dissociated
     * @param providerIds List of Providers which will be dissociated from a specific Provider
     */
    public void dissociateProviderFromProject(String projectId, Set<String> providerIds){

        projectRepository.dissociateProviderFromProject(projectId, providerIds);
    }

    public List<HierarchicalRelationProjection> hierarchicalStructure(final String externalId) {

        return projectRepository.hierarchicalStructure(externalId);
    }

    public InformativeResponse grantPermissionToProvider(String projectId, String providerId, String who, AccessControlRequestDto request, Collection collection){

        projectRepository.accessibility(projectId);

        var accessControl = AccessControlMapper.INSTANCE.requestToAccessControl(request);

        accessControl.setEntity(projectId + HierarchicalRelation.PATH_SEPARATOR + providerId);

        accessControl.setWho(who);

        accessControl.setCollection(collection);

        accessControlRepository.persist(accessControl);

        var informativeResponse = new InformativeResponse();
        informativeResponse.message = "Access Control entry has been created successfully";
        informativeResponse.code = 200;

        return informativeResponse;
    }

    public AccessControlResponseDto modifyProviderPermission(String projectId, String providerId, String who, AccessControlUpdateDto updateDto, Collection collection){

        projectRepository.accessibility(projectId);

        var accessControl = accessControlRepository.findByWhoAndCollectionAndEntity(who, collection, projectId + HierarchicalRelation.PATH_SEPARATOR + providerId);

        AccessControlMapper.INSTANCE.updateAccessControlFromDto(updateDto, accessControl);

        accessControlRepository.update(accessControl);

        return AccessControlMapper.INSTANCE.accessControlToResponse(accessControl);
    }

    public InformativeResponse deleteProviderPermission(String projectId, String providerId, String who, Collection collection){

        projectRepository.accessibility(projectId);

        var accessControl = accessControlRepository.findByWhoAndCollectionAndEntity(who, collection, projectId + HierarchicalRelation.PATH_SEPARATOR + providerId);

        accessControlRepository.delete(accessControl);

        var successResponse = new InformativeResponse();
        successResponse.code = 200;
        successResponse.message = "Access Control entry has been deleted successfully.";

        return successResponse;
    }

    public AccessControlResponseDto fetchProviderPermission(String projectId, String providerId, String who){

        projectRepository.accessibility(projectId);

        var accessControl = accessControlRepository.findByWhoAndCollectionAndEntity(projectId + HierarchicalRelation.PATH_SEPARATOR + providerId, Collection.Provider, who);

        return AccessControlMapper.INSTANCE.accessControlToResponse(accessControl);
    }
}
