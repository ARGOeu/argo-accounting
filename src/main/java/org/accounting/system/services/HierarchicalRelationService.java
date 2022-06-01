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

    public PageResource<MetricProjection, MetricProjection> fetchAllMetrics(String id, int page, int size, UriInfo uriInfo){

        var projection = hierarchicalRelationRepository.findByExternalId(id, page, size);

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
