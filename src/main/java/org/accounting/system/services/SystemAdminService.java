package org.accounting.system.services;

import org.accounting.system.clients.ProjectClient;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.accounting.system.repositories.project.ProjectModulator;
import org.accounting.system.repositories.project.ProjectRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Set;

@ApplicationScoped
public class SystemAdminService {

    @Inject
    RoleRepository roleRepository;

    @Inject
    @RestClient
    ProjectClient projectClient;

    @Inject
    ProjectRepository projectRepository;

    public void accessListOfProjects(Set<String> projects, String who){

        for(String projectID:projects){

            RoleAccessControl accessControl = new RoleAccessControl();
            accessControl.setWho(who);
            accessControl.setCreatorId(who);
            accessControl.setRoles(roleRepository.getRolesByName(Set.of("project_admin")));

            var function = ProjectModulator.openAire();

            var project = function.apply(projectID, projectClient);

            project.setCreatorId(who);
            projectRepository.persist(project);

            projectRepository.insertNewRoleAccessControl(projectID, accessControl);
        }
    }
}
