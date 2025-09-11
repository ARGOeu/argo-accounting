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
import org.accounting.system.util.QueryParser;

import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class ProjectService {

    @Inject
    ProjectRepository projectRepository;

    @Inject
    QueryParser queryParser;

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