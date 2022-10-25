package org.accounting.system.repositories.project;

import com.pivovarit.function.ThrowingBiFunction;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.entities.Project;
import org.accounting.system.mappers.ProjectMapper;
import org.accounting.system.repositories.modulators.AbstractModulator;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.Objects;
import java.util.Set;


public class ProjectModulator extends AbstractModulator<Project, String> {


    @Inject
    ProjectAccessEntityRepository projectAccessEntityRepository;

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

    @Override
    public ProjectAccessAlwaysRepository always() {
        return projectAccessAlwaysRepository;
    }

    @Override
    public ProjectAccessEntityRepository entity() {
        return projectAccessEntityRepository;
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
