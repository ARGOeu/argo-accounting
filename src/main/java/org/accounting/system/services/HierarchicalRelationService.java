package org.accounting.system.services;

import com.mongodb.MongoWriteException;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.Metric;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.enums.RelationType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

        metric.setInfrastructure(storedInstallation.getInfrastructure());

        try{
            metricRepository.persist(metric);
        } catch (MongoWriteException e) {
            throw new ConflictException(String.format("There is a Metric at {%s, %s, %s} with the following attributes : {%s, %s, %s}",
                    metric.getProject(), metric.getProvider(), metric.getInstallation(),
                    request.metricDefinitionId, request.start, request.end));
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
