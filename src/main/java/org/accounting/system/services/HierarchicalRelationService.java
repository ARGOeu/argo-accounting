package org.accounting.system.services;

import com.mongodb.MongoWriteException;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.dtos.metric.MetricResponseDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.enums.RelationType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.ProjectRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
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

    /**
     * This method is responsible for creating a hierarchical structure which consists of :
     * - Root -> Project
     * - Intermediate Level -> Provider
     * - Leaf -> Installation
     * The Metric is assigned to the given installation (leaf)
     *
     * @param projectId Root of structure
     * @param providerId Intermediate level of structure
     * @param installationId Leaf of structure
     * @param request Metric to be created and assigned to the given installation
     * @return The assigned Metric
     */
    public MetricResponseDto assign(String projectId, String providerId, String installationId, MetricRequestDto request) {

        HierarchicalRelation project = new HierarchicalRelation(projectId, RelationType.PROJECT);

        HierarchicalRelation provider = new HierarchicalRelation(providerId, project, RelationType.PROVIDER);

        HierarchicalRelation installation = new HierarchicalRelation(installationId, provider, RelationType.INSTALLATION);

        var metric = MetricMapper.INSTANCE.requestToMetric(request);

        metric.setResourceId(installation.id);

        metric.setProject(projectRepository.findById(projectId).getAcronym());

        metric.setProvider(providerRepository.findById(providerId).getId());

        metric.setInstallation(installationRepository.findById(new ObjectId(installationId)).getInstallation());

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

        return MetricMapper.INSTANCE.metricToResponse(metric);
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

    /**
     * This method correlates the given Providers with a specific Project and creates an hierarchical structure with root
     * the given Project and children the given Providers.
     *
     * @param projectId The Project id with which the Providers are to be correlated.
     * @param providerIds List of Providers which will be correlated with a specific Provider
     * @throws NotFoundException If a Provider doesn't exist
     */
    public void createProjectProviderRelationship(String projectId, Set<String> providerIds){

        for(String providerId : providerIds){
            providerRepository.findByIdOptional(providerId).orElseThrow(()->new NotFoundException("There is no Provider with the following id: "+providerId));
        }

        for(String providerId : providerIds){

            HierarchicalRelation project = new HierarchicalRelation(projectId, RelationType.PROJECT);
            HierarchicalRelation provider = new HierarchicalRelation(providerId, project, RelationType.PROVIDER);

            hierarchicalRelationRepository.save(project, null);
            hierarchicalRelationRepository.save(provider, null);
        }
    }

    public List<HierarchicalRelationProjection> hierarchicalStructure(final String externalId) {

        return hierarchicalRelationRepository.hierarchicalStructure(externalId);
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
