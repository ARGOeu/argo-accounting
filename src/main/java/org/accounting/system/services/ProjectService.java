package org.accounting.system.services;

import org.accounting.system.clients.ProjectClient;
import org.accounting.system.dtos.project.ProjectResponseDto;
import org.accounting.system.mappers.ProjectMapper;
import org.accounting.system.repositories.ProjectRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.Objects;

@ApplicationScoped
public class ProjectService {

    @Inject
    @RestClient
    ProjectClient projectClient;

    @Inject
    ProjectRepository projectRepository;

    /**
     * An http call is made to Open Aire to retrieve the corresponding Project.
     * It is then stored in the database, converted into a response body and returned.
     *
     * @param id The Project id
     * @return The Project as it is stored in the Accounting system database.
     */
    public ProjectResponseDto getById(String id){

        var responseFromOpenAire = projectClient.getById(id, "json");

        if(Objects.isNull(responseFromOpenAire.response.results)){
            throw new NotFoundException("Project with id {"+id+"} not found.");
        }

        var project = ProjectMapper.INSTANCE.openAireResponseToProject(responseFromOpenAire);

        projectRepository.persistOrUpdate(project);

        return ProjectMapper.INSTANCE.projectToDto(project);
    }
}
