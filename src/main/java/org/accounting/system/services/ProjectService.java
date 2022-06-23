package org.accounting.system.services;

import org.accounting.system.clients.ProjectClient;
import org.accounting.system.dtos.project.ProjectResponseDto;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.mappers.ProjectMapper;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ProjectService {

    @Inject
    @RestClient
    ProjectClient projectClient;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    InstallationRepository installationRepository;

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
}
