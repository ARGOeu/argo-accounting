package org.accounting.system.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.Project;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.ProjectReport;
import org.accounting.system.entities.projections.normal.ProjectProjection;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.services.clients.GroupRequest;
import org.accounting.system.util.QueryParser;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProjectService {

    private static final Logger LOG = Logger.getLogger(ProjectService.class);

    @Inject
    ProjectRepository projectRepository;

    @Inject
    QueryParser queryParser;

    @Inject
    KeycloakService keycloakService;

    /**
     * This method correlates the given Providers with a specific Project and creates a hierarchical structure with root
     * the given Project and children the given Providers.
     *
     * @param projectId The Project id with which the Providers are going to be correlated.
     * @param providerId Provider which will be correlated with a specific Project
     * @throws NotFoundException If a Provider doesn't exist
     */
    public void associateProjectWithProvider(String projectId, String providerId){

        projectRepository.associateProjectWithProvider(projectId, providerId);

        try{

            var groupRequest = new GroupRequest();
            groupRequest.name = providerId;

            GroupRequest.Attributes attrs = new GroupRequest.Attributes();
            attrs.description = List.of(String.format("Provider %s associated with Project %s",providerId, projectId));

            groupRequest.attributes = attrs;
            keycloakService.createSubGroup(keycloakService.getValueByKey(String.format("/accounting/%s", projectId)), groupRequest);
            var id = keycloakService.getValueByKey(String.format("/accounting/%s/%s", projectId, providerId));
            keycloakService.addRole(id, "admin");
            keycloakService.addRole(id, "viewer");

            var defaultConfigurationId = keycloakService.getValueByKey(id);
            var defaultConfiguration = keycloakService.getConfiguration(id, defaultConfigurationId);
            var groupRoles = List.of("admin", "viewer");
            defaultConfiguration.setGroupRoles(groupRoles);
            keycloakService.updateConfiguration(id, defaultConfiguration);
        } catch (Exception e){

            projectRepository.dissociateProviderFromProject(projectId, providerId);
            LOG.error("Group creation failed with error : " + e.getMessage());
        }
    }

    /**
     * This method dissociated Providers from a specific Project.
     *
     * @param projectId The Project id from which the Providers are going to be dissociated
     * @param providerId Provider which will be dissociated from a specific Provider
     */
    public void dissociateProviderFromProject(String projectId, String providerId){

        projectRepository.dissociateProviderFromProject(projectId, providerId);

        var key = String.format("/accounting/%s/%s", projectId, providerId);

        try{

            keycloakService.deleteGroup(keycloakService.getValueByKey(key));
        } catch (Exception e){

            LOG.error(String.format("Group deletion %s failed with error : %s", key, e.getMessage()));
        }
    }

    public ProjectProjection getById(final String id) {

         return projectRepository.fetchById(id);
    }

    public PageResource<InstallationResponseDto> getInstallationsByProject(String projectId, int page, int size, UriInfo uriInfo){

        PanacheQuery<InstallationProjection> projectionQuery = projectRepository.fetchProjectInstallations(projectId, page, size);

        return new PageResource<>(projectionQuery, InstallationMapper.INSTANCE.installationProjectionsToResponse(projectionQuery.list()), uriInfo);
    }

    public  PageResource<ProjectProjection> searchProject(String json, int page, int size, UriInfo uriInfo) throws org.json.simple.parser.ParseException {

        var query = queryParser.parseFile(json);
        var projectionQuery = projectRepository.searchProjects(query,page,size);
        return new PageResource<>(projectionQuery, projectionQuery.list(), uriInfo);
    }

    public PageResource<ProjectProjection> getAll(int page, int size, UriInfo uriInfo) throws NoSuchFieldException, org.json.simple.parser.ParseException, JsonProcessingException {

       var projectionQuery = projectRepository.fetchAll(page, size);

       return new PageResource<>(projectionQuery, projectionQuery.list(), uriInfo);
    }

    public PageResource<MetricDefinitionResponseDto> fetchAllMetricDefinitions(String id, int page, int size, UriInfo uriInfo){

        var projection = projectRepository.fetchAllMetricDefinitions(id, page, size);

        return new PageResource<>(projection, MetricDefinitionMapper.INSTANCE.metricDefinitionsToResponse(projection.list()), uriInfo);
    }

    public PageResource<ProjectProjection> getAllForSystemAdmin(int page, int size, UriInfo uriInfo) {

        var projectionQuery = projectRepository.fetchAllForSystemAdmin(page, size);

        return new PageResource<>(projectionQuery, projectionQuery.list(), uriInfo);
    }

    public ProjectReport projectReport(String projectId, String start, String end){


        return projectRepository.projectReport(projectId, start, end);
    }

    public Optional<Project> findByIdOptional(String id){

        return  projectRepository.findByIdOptional(id);
    }
}