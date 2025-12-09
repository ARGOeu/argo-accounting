package org.accounting.system.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.model.Filters;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.vavr.collection.Array;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.dtos.installation.InstallationResponseDto;
import org.accounting.system.dtos.metricdefinition.MetricDefinitionResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.Project;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MetricReportProjection;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.entities.projections.ProjectProjectionWithPermissions;
import org.accounting.system.entities.projections.ProjectReport;
import org.accounting.system.entities.projections.normal.ProjectProjection;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.mappers.MetricDefinitionMapper;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.services.groupmanagement.EntitlementServiceFactory;
import org.accounting.system.services.groupmanagement.GroupManagementSelection;
import org.accounting.system.util.QueryParser;
import org.accounting.system.util.Utility;
import org.bson.conversions.Bson;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProjectService {

    private static final Logger LOG = Logger.getLogger(ProjectService.class);

    @Inject
    ProjectRepository projectRepository;

    @Inject
    QueryParser queryParser;

    @Inject
    GroupManagementSelection groupManagementSelection;

    @Inject
    ProviderService providerService;

    @Inject
    EntitlementServiceFactory entitlementServiceFactory;

    @Inject
    ClientService clientService;

    @Inject
    RequestUserContext requestUserContext;

    @Inject
    Utility utility;

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

            groupManagementSelection.from().choose().createAssociationGroup(projectId, providerId);
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

        groupManagementSelection.from().choose().deleteAssociationGroup(projectId, providerId);
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

        return projectReport(projectId, start, end, Array.of());
    }

    public ProjectReport projectReport(String projectId, String start, String end, Array<Bson> filters){

        var project = projectRepository.findById(projectId);

        var providers = projectRepository.fetchProjectProviders(projectId);

        var providerReports = providers.stream().map(provider->providerService.providerReport(projectId, provider.getId(), start, end, filters)).toList();

        var report = new ProjectReport();
        report.id = projectId;
        report.endDate = project.getEndDate();
        report.startDate = project.getStartDate();
        report.title = project.getTitle();
        report.callIdentifier = project.getCallIdentifier();
        report.acronym = project.getAcronym();
        report.data = providerReports;

        var aggregatedMetrics = providerReports
                .stream()
                .flatMap(rpt -> rpt.aggregatedMetrics.stream())
                .collect(Collectors.toMap(k->k.metricDefinitionId,
                        m -> {
                            var copy = new MetricReportProjection();
                            copy.metricDefinitionId = m.metricDefinitionId;
                            copy.metricName = m.metricName;
                            copy.metricDescription = m.metricDescription;
                            copy.unitType = m.unitType;
                            copy.metricType = m.metricType;
                            copy.totalValue = m.totalValue;
                            return copy;
                        },
                        (m1, m2) -> {
                            m1.totalValue = m1.totalValue + m2.totalValue;
                            return m1;
                        }
                ));

        report.aggregatedMetrics = new ArrayList<>(aggregatedMetrics.values());

        return report;
    }

    public ProjectReport projectReportByGroupId(String projectId, String groupId, String start, String end){

        return projectReport(projectId, start, end, Array.of(Filters.eq("group_id", groupId)));
    }

    public ProjectReport projectReportByUserId(String projectId, String userId, String start, String end){

        return projectReport(projectId, start, end, Array.of( Filters.eq("user_id", userId)));
    }

    public Optional<Project> findByIdOptional(String id){

        return  projectRepository.findByIdOptional(id);
    }

    public PageResource<ProjectProjectionWithPermissions> getClientPermissions(int page, int size, UriInfo uriInfo){

        var entitlements = entitlementServiceFactory.from().fetchEntitlements();

        var list = new ArrayList<ProjectProjectionWithPermissions>();

        if(requestUserContext.getOidcTenantConfig().isEmpty() && entitlementServiceFactory.from().hasAccess(requestUserContext.getParent(), "admin", List.of())){

            var db = projectRepository.fetchClientPermissions(page, size);

            var projects = db.list();

            projects.forEach(pr->pr.permissions = clientService.projectAdmin());

            return new PageResource<>(db, projects, uriInfo);
        }

        entitlements.forEach(en->{

            var raw = parseEntitlementLevels(en.getRaw());

            if(raw.size() == 1){

                var projectionQuery = projectRepository.fetchClientPermissions(raw.get(0));

                if(projectionQuery != null){

                    projectionQuery.permissions = clientService.projectAdmin();
                    list.add(projectionQuery);
                }
            } else if (raw.size() == 2){

                var projectionQuery = projectRepository.fetchClientPermissions(raw.get(0), raw.get(1));

                if(projectionQuery != null && projectionQuery.providers!=null){

                    projectionQuery.providers.forEach(provider->provider.permissions = clientService.providerAdmin());
                    list.add(projectionQuery);
                }

            } else if (raw.size() == 3){

                var projectionQuery = projectRepository.fetchClientPermissions(raw.get(0), raw.get(1), raw.get(2));

                if(projectionQuery != null && projectionQuery.providers != null){

                    var installations = projectionQuery
                            .providers
                            .stream()
                            .flatMap(pr -> pr.installations.stream())
                            .toList();

                    installations.forEach(installation -> installation.permissions = clientService.installationAdmin());

                    list.add(projectionQuery);
                }

            }
        });

        var partition = utility.partition(list, size);

        var permissions = partition.get(page) == null ? Collections.EMPTY_LIST : partition.get(page);

        var pageable = new MongoQuery<ProjectProjectionWithPermissions>();

        pageable.list = permissions;
        pageable.index = page;
        pageable.size = size;
        pageable.count = list.size();
        pageable.page = Page.of(page, size);

        return new PageResource<>(pageable, pageable.list, uriInfo);
    }

    private String extractProjectId(String entitlement) {
        String[] parts = entitlement.split(":");

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(requestUserContext.getParent()) && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return "";
    }

    public List<String> parseEntitlementLevels(String urn) {

        String[] parts = urn.split(":");

        int startIndex = -1;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(requestUserContext.getParent())) {
                startIndex = i + 1;
                break;
            }
        }
        if (startIndex == -1) {
            return List.of();
        }

        List<String> levels = new ArrayList<>();
        for (int i = startIndex; i < parts.length; i++) {
            if (parts[i].startsWith("role=")) break;
            levels.add(parts[i]);
        }

        return levels;
    }
}