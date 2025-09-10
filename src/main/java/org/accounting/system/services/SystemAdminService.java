package org.accounting.system.services;

import com.mongodb.client.model.Filters;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.project.ProjectRequest;
import org.accounting.system.dtos.project.UpdateProjectRequest;
import org.accounting.system.dtos.resource.ResourceRequest;
import org.accounting.system.dtos.resource.ResourceResponse;
import org.accounting.system.dtos.tenant.OidcTenantConfigRequest;
import org.accounting.system.dtos.tenant.OidcTenantConfigResponse;
import org.accounting.system.dtos.tenant.UpdateOidcTenantConfig;
import org.accounting.system.entities.Project;
import org.accounting.system.entities.projections.normal.ProjectProjection;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.OidcTenantConfigMapper;
import org.accounting.system.mappers.ResourceMapper;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.OidcTenantConfigRepository;
import org.accounting.system.repositories.ResourceRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.project.ProjectModulator;
import org.accounting.system.repositories.project.ProjectRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class SystemAdminService {

    @Inject
    @RestClient
    ProjectClient projectClient;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    ResourceRepository resourceRepository;

    @Inject
    MetricRepository metricRepository;

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    @Inject
    InstallationRepository installationRepository;

    @Inject
    OidcTenantConfigRepository oidcTenantConfigRepository;


    /**
     * This method is responsible for registering several Projects into Accounting Service.
     * Those Projects are also assigned to all system admins.
     *
     * @param projects A list of Projects to be registered.
     * @param who      The system admin who performs the assignment.
     */
    public InformativeResponse registerProjectsToAccountingService(Set<String> projects, String who) {

        var response = new InformativeResponse();

        var success = new HashSet<String>();
        var errors = new HashSet<String>();

        for (String projectID : projects) {

            var optional = projectRepository.findByIdOptional(projectID);

            if (optional.isEmpty()) {

                try {

                    var function = ProjectModulator.openAire();

                    var project = function.apply(projectID, projectClient);

                    project.setCreatorId(who);

                    projectRepository.persist(project);

                    success.add(projectID);

                } catch (NotFoundException nfe) {

                    errors.add(nfe.getMessage());

                } catch (Exception e) {

                    errors.add(String.format("Project : %s has not been registered. Please try again.", projectID));
                }
            } else {

                errors.add(String.format("Project %s has already been registered.", projectID));
            }
        }

        if (success.isEmpty()) {

            response.message = "No project registration was performed";
        } else {

            response.message = "Project(s) : " + success + " registered successfully.";
        }

        response.code = 200;
        response.errors = errors;

        return response;
    }

    public void deleteProject(String id){

        metricRepository.deleteByProjectId(id);
        hierarchicalRelationRepository.deleteByProjectId(id);
        projectRepository.deleteById(id);

    }

    public void deleteResource(String id){

        if(installationRepository.resourceExists(id)){

            throw new ConflictException("Resource cannot be deleted. It is assigned to an Installation.");
        }

        resourceRepository.deleteById(id);
    }

    /**
     * Creates a new Project.
     *
     * @param request The data transfer object containing project details.
     * @param who     The system admin who performs the request.
     * @return The created Project.
     */
    public ProjectProjection createProject(ProjectRequest request, String who) {

        var function = ProjectModulator.openAireOptional();

        var eeProject = function.apply(request.id, projectClient);

        if (eeProject.isPresent()) {

            throw new ConflictException(String.format("Project with ID [%s] exists in European Database.", request.id));
        }

        var databaseProject = projectRepository.findByIdOptional(request.id);

        if (databaseProject.isPresent()) {

            throw new ConflictException(String.format("Project with ID [%s] has already been registered in Accounting Service.", request.id));
        }

        var project = new Project();
        project.setId(request.id);
        project.setTitle(request.title);
        project.setAcronym(request.acronym);
        project.setEndDate(request.endDate);
        project.setStartDate(request.startDate);
        project.setCallIdentifier(request.callIdentifier);
        project.setCreatorId(who);
        project.setRegisteredOn(LocalDateTime.now());

        projectRepository.persist(project);

        return projectRepository.fetchById(project.getId());
    }

    /**
     * Creates a new Resource.
     *
     * @param request The data transfer object containing resource details.
     * @return The created Resource.
     */
    public ResourceResponse createResource(ResourceRequest request) {

        var databaseResource = resourceRepository.findByIdOptional(request.id);

        if (databaseResource.isPresent()) {

            throw new ConflictException(String.format("Resource with ID [%s] has already been registered in Accounting Service.", request.id));
        }

        var resource = ResourceMapper.INSTANCE.dtoToResource(request);

        resourceRepository.persist(resource);

        return ResourceMapper.INSTANCE.resourceToDto(resource);
    }

    public ProjectProjection updateProject(UpdateProjectRequest request, String id) {

        return projectRepository.updateProject(request, id);
    }

    /**
     * Retrieves the number of documents inserted in a collection within the specified time period.
     * The method extracts timestamps from MongoDB ObjectIds and filters records based on the given date range. The response includes the count of collection documents.
     *
     * @param collectionName The collection name.
     * @param start          The start date in the format "YYYY-MM-DD". This defines the lower bound of the time range.
     * @param end            The end date in the format "YYYY-MM-DD". This defines the upper bound of the time range.
     * @return The count of documents in collection within the specified range.
     **/
    public long countDocuments(String collectionName, Date start, Date end) {

        var startId = new ObjectId(Long.toHexString(start.getTime() / 1000) + "0000000000000000");
        var endId = new ObjectId(Long.toHexString(end.getTime() / 1000) + "0000000000000000");

        return projectRepository.getMongoCollection(collectionName).countDocuments(Filters.and(Filters.gte("_id", startId), Filters.lte("_id", endId)));
    }

    /**
     * Creates a new OIDC tenant configuration.
     *
     * @param request The data transfer object containing tenant configuration.
     * @return The created OIDC tenant configuration.
     */
    public OidcTenantConfigResponse createOidcTenantConfig(OidcTenantConfigRequest request) {

        var optionalIssuer = oidcTenantConfigRepository.fetchOidcTenantConfigByIssuer(request.issuer);

        if (optionalIssuer.isPresent()) {

            throw new ConflictException(String.format("OIDC tenant configuration with issuer [%s] has already been registered in Accounting Service.", request.issuer));
        }

        var optionalTenantId = oidcTenantConfigRepository.fetchOidcTenantConfigByTenant(request.tenantId);

        if (optionalTenantId.isPresent()) {

            throw new ConflictException(String.format("OIDC tenant configuration with tenant ID [%s] has already been registered in Accounting Service.", request.tenantId));
        }

        var config = OidcTenantConfigMapper.INSTANCE.dtoToConfig(request);

        oidcTenantConfigRepository.persist(config);

        return OidcTenantConfigMapper.INSTANCE.configToDto(config);
    }

    public OidcTenantConfigResponse fetchOidcTenantConfig(String id){

        var config = oidcTenantConfigRepository.findById(new ObjectId(id));

        return OidcTenantConfigMapper.INSTANCE.configToDto(config);
    }

    public PageResource<OidcTenantConfigResponse> findAllOidcConfigPageable(int page, int size, UriInfo uriInfo){

        var panacheQuery = oidcTenantConfigRepository
                .findAll()
                .page(Page.of(page, size));

        return new PageResource<>(panacheQuery, OidcTenantConfigMapper.INSTANCE.configsToResponse(panacheQuery.list()), uriInfo);
    }

    public void deleteOidcTenantConfig(String id){

        oidcTenantConfigRepository.deleteById(new ObjectId(id));
    }

    public OidcTenantConfigResponse updateOidcTenantConfig(UpdateOidcTenantConfig request, String id) {

        var entity = oidcTenantConfigRepository.findById(new ObjectId(id));

        if (StringUtils.isNotEmpty(request.tenantId) && !request.tenantId.equals(entity.getTenantId())) {
            if (oidcTenantConfigRepository.fetchOidcTenantConfigByTenant(request.tenantId).isPresent()) {
                throw new ConflictException("Tenant ID already in use.");
            }
        }

        if (StringUtils.isNotEmpty(request.issuer) && !request.issuer.equals(entity.getIssuer())) {
            if (oidcTenantConfigRepository.fetchOidcTenantConfigByIssuer(request.issuer).isPresent()) {
                throw new ConflictException("Issuer already in use.");
            }
        }

        OidcTenantConfigMapper.INSTANCE.updateConfigFromDto(request, entity);

        oidcTenantConfigRepository.update(entity);

        return OidcTenantConfigMapper.INSTANCE.configToDto(entity);
    }
}