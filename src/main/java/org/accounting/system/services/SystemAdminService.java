package org.accounting.system.services;

import com.mongodb.MongoWriteException;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.client.Client;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.repositories.authorization.RoleRepository;
import org.accounting.system.repositories.client.ClientRepository;
import org.accounting.system.repositories.project.ProjectModulator;
import org.accounting.system.repositories.project.ProjectRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class SystemAdminService {

    @Inject
    RoleRepository roleRepository;

    @Inject
    ClientRepository clientRepository;

    @Inject
    @RestClient
    ProjectClient projectClient;

    @Inject
    ProjectRepository projectRepository;

    /**
     * This method is responsible for registering several Projects into Accounting Service.
     * Those Projects are also assigned to all system admins.
     * @param projects A list of Projects to be registered.
     * @param who The system admin who performs the assignment.
     */
    public void registerProjectsToAccountingService(Set<String> projects, String who){

        for(String projectID:projects){

            var projectAdminRole = roleRepository.getRolesByName(Set.of("project_admin"));

            var function = ProjectModulator.openAire();

            var project = function.apply(projectID, projectClient);

            project.setCreatorId(who);

            try{
                projectRepository.persist(project);
            } catch (MongoWriteException e){
                throw new ConflictException("This Project has already been registered.");
            }

            var systemAdmins = clientRepository.getSystemAdmins();

            var systemAdminsAccessControls = systemAdmins
                    .stream()
                    .map(Client::getId)
                    .map(id->{

                        var systemAdminAccessControl = new RoleAccessControl();
                        systemAdminAccessControl.setWho(id);
                        systemAdminAccessControl.setCreatorId(who);
                        systemAdminAccessControl.setRoles(projectAdminRole);
                        return systemAdminAccessControl;
                    }).collect(Collectors.toSet());

            projectRepository.insertListOfRoleAccessControl(projectID, systemAdminsAccessControls);
        }
    }
}