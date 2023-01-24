package org.accounting.system.repositories.project;

import com.pivovarit.function.ThrowingBiFunction;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.entities.Project;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.repositories.modulators.AccessibleModulator;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

@ApplicationScoped
public class ProjectAccessAlwaysRepository extends AccessibleModulator<Project, String> {

    @Inject
    @RestClient
    ProjectClient projectClient;

    public Project save(String id, ThrowingBiFunction<String, ProjectClient, Project, NotFoundException> retrieveFromOpenAire){

        findByIdOptional(id).ifPresent(project -> {throw new ConflictException("The Project with id {"+project.getId()+"} has already been registered.");});

        var project = retrieveFromOpenAire.apply(id, projectClient);

        persistOrUpdate(project);

        return project;
    }
}
