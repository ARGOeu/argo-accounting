package org.accounting.system.repositories.project;

import com.pivovarit.function.ThrowingBiFunction;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.entities.Project;
import org.accounting.system.mappers.ProjectMapper;
import org.accounting.system.repositories.modulators.AccessibleModulator;

import java.util.Objects;
import java.util.Set;


public class ProjectModulator extends AccessibleModulator<Project, String> {

    @Inject
    ProjectRepository projectRepository;

    @Inject
    ProjectAccessAlwaysRepository projectAccessAlwaysRepository;

    public Project save(String id){

        return projectAccessAlwaysRepository.save(id, openAire());

    }

    public void associateProjectWithProviders(String projectId, Set<String> providerIds){

        projectRepository.associateProjectWithProviders(projectId, providerIds);

    }

    public void dissociateProviderFromProject(String projectId, Set<String> providerIds){

        projectRepository.dissociateProviderFromProject(projectId, providerIds);
    }

    public static ThrowingBiFunction<String, ProjectClient, Project, NotFoundException> openAire() {

        return (id, client) -> {
            var responseFromOpenAire = client.getById(id, "json");

            if(Objects.isNull(responseFromOpenAire.response.results)){
                throw new NotFoundException("Project with id {"+id+"} not found.");
            }

            return ProjectMapper.INSTANCE.openAireResponseToProject(responseFromOpenAire);
        };
    }
}
