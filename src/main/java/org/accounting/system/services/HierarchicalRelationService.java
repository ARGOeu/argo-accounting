package org.accounting.system.services;

import com.mongodb.MongoWriteException;
import org.accounting.system.beans.RequestInformation;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.Metric;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.projections.ProjectionQuery;
import org.accounting.system.enums.RelationType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.interceptors.annotations.AccessPermissionsUtil;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.installation.InstallationAccessAlwaysRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.Set;

@ApplicationScoped
public class HierarchicalRelationService {

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    InstallationRepository installationRepository;

    @Inject
    MetricRepository metricRepository;

    @Inject
    RequestInformation requestInformation;

    @Inject
    InstallationAccessAlwaysRepository installationAccessAlwaysRepository;

    public Metric assignMetric(String installationId, MetricRequestDto request) {

        var storedInstallation = installationRepository.findById(new ObjectId(installationId));

        HierarchicalRelation project = new HierarchicalRelation(storedInstallation.getProject(), RelationType.PROJECT);

        HierarchicalRelation provider = new HierarchicalRelation(storedInstallation.getOrganisation(), project, RelationType.PROVIDER);

        HierarchicalRelation installation = new HierarchicalRelation(installationId, provider, RelationType.INSTALLATION);

        var metric = MetricMapper.INSTANCE.requestToMetric(request);

        metric.setResourceId(installation.id);

        metric.setProject(projectRepository.findById(storedInstallation.getProject()).getAcronym());

        metric.setProvider(providerRepository.findById(storedInstallation.getOrganisation()).getId());

        metric.setInstallation(storedInstallation.getInstallation());

        try{
            metricRepository.persist(metric);
        } catch (MongoWriteException e) {
            throw new ConflictException(String.format("There is a Metric at {%s, %s, %s} with the following attributes : {%s, %s, %s, %s}",
                    metric.getProject(), metric.getProvider(), metric.getInstallation(),
                    request.metricDefinitionId, request.start, request.end, request.value));
        }

        hierarchicalRelationRepository.save(project, null);
        hierarchicalRelationRepository.save(provider, null);
        hierarchicalRelationRepository.save(installation, metric.getId());

        return metric;
    }

    /**
     * This method delegates to {@link HierarchicalRelationRepository hierarchicalRelationRepository} to check if the given Provider belongs to a specific Project
     *
     * checks if the given Provider belongs to a specific Project
     *
     * @param projectId The Project ID.
     * @param providerId The Provider ID.
     * @return if provider belongs to project.
     */
    public boolean providerBelongsToProject(String projectId, String providerId){

        return hierarchicalRelationRepository.hierarchicalRelationshipExists(projectId + HierarchicalRelation.PATH_SEPARATOR + providerId);
    }

    /**
     * This method delegates to {@link HierarchicalRelationRepository hierarchicalRelationRepository} to check if the given hierarchical relationship exists
     **
     * @param projectId The Project ID.
     * @param providerId The Provider ID.
     * @param installationId The Installation ID.
     * @throws BadRequestException If the  projectId.providerId.installationId hierarchical relationship doesn't exist.
     */
    public void hierarchicalRelationshipExists(String projectId, String providerId, String installationId){

        var exists = hierarchicalRelationRepository.hierarchicalRelationshipExists(projectId + HierarchicalRelation.PATH_SEPARATOR + providerId + HierarchicalRelation.PATH_SEPARATOR + installationId);

        if(!exists){
            String message = String.format("There is no relationship among {%s, %s, %s}",projectId, providerId, installationId);
            throw new BadRequestException(message);
        }
    }

    public PageResource<MetricProjection, MetricProjection> fetchAllMetricsByProjectId(String id, int page, int size, UriInfo uriInfo){

        ProjectionQuery<MetricProjection> projection = projectRepository.fetchAllMetricsByProjectId(id, page, size);

        return new PageResource<>(projection, projection.list, uriInfo);
    }

    public PageResource<MetricProjection, MetricProjection> fetchAllMetricsUnderAProvider(String projectId, String providerId, int page, int size, UriInfo uriInfo){

        accessibilityOfProjectAndProvider(projectId, providerId);

        var projection = hierarchicalRelationRepository.findByExternalId(projectId + HierarchicalRelation.PATH_SEPARATOR + providerId, page, size);

        if(projection.count == 0){
            throw new NotFoundException("No metrics added.");
        }

        return new PageResource<>(projection, projection.list, uriInfo);
    }

    public PageResource<MetricProjection, MetricProjection> fetchAllMetricsUnderAnInstallation(String installationId, int page, int size, UriInfo uriInfo){

        var installation = installationAccessAlwaysRepository.findById(new ObjectId(installationId));

        accessibilityOfProjectAndProvider(installation.getProject(), installation.getOrganisation());

        var projection = hierarchicalRelationRepository.findByExternalId(installation.getProject() + HierarchicalRelation.PATH_SEPARATOR + installation.getOrganisation() + HierarchicalRelation.PATH_SEPARATOR + installationId, page, size);

        if(projection.count == 0){
            throw new NotFoundException("No metrics added.");
        }

        return new PageResource<>(projection, projection.list, uriInfo);
    }

    private void accessibilityOfProjectAndProvider(String project, String provider){

        Boolean accessibility = null;

        int count = 0;

        for(AccessPermissionsUtil util: requestInformation.getAccessPermissions()) {

            requestInformation.setAccessType(util.getAccessType());

            try{
                switch (util.getCollection()) {
                    case Project:
                        accessibility = projectRepository.accessibility(project);
                        break;
                    case Provider:
                        accessibility = providerRepository.accessibility(project, provider);
                        break;
                    default:
                        throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
                }
            } catch (ForbiddenException e){
                count++;
            }

            if(accessibility != null){
                break;
            }
        }

        if(count == requestInformation.getAccessPermissions().size()){
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public PageResource<MetricProjection, MetricProjection> fetchAllMetrics(String id, int page, int size, UriInfo uriInfo){

        var projection = hierarchicalRelationRepository.findByExternalId(id, page, size);

        if(projection.count == 0){
            throw new NotFoundException("No metrics added.");
        }

        return new PageResource<>(projection, projection.list, uriInfo);
    }

    public void recursion(HierarchicalRelationProjection relation, Set<Document> metrics){

        metrics.addAll(relation.metrics);

        if (relation.hierarchicalRelations.isEmpty()) {
            return;
        }

        for (HierarchicalRelationProjection rel : relation.hierarchicalRelations) {
            recursion(rel, metrics );
        }
    }
}
