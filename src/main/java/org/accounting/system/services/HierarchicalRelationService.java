package org.accounting.system.services;

import com.mongodb.MongoWriteException;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.Metric;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.MetricMapper;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.metric.MetricRepository;
import org.accounting.system.repositories.project.ProjectRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.installation.InstallationService;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HierarchicalRelationService {

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    InstallationService installationService;

    @Inject
    MetricRepository metricRepository;

    public Metric assignMetric(String installationId, MetricRequestDto request) {

        var storedInstallation = installationService.fetchInstallation(installationId);

        var hierarchicalRelation = hierarchicalRelationRepository.findByExternalId(installationId);

        var metric = MetricMapper.INSTANCE.requestToMetric(request);

        if(StringUtils.isNotEmpty(storedInstallation.getResource())){
            metric.setResource(storedInstallation.getResource());
        }

        metric.setResourceId(hierarchicalRelation.id);

        metric.setProject(projectRepository.findById(storedInstallation.getProject()).getAcronym());

        metric.setProvider(providerRepository.findById(storedInstallation.getOrganisation()).getId());

        metric.setInstallation(storedInstallation.getInstallation());

        metric.setInfrastructure(storedInstallation.getInfrastructure());
        metric.setProjectId(storedInstallation.getProject());
        metric.setInstallationId(storedInstallation.getId());

        try{
            metricRepository.persist(metric);
            hierarchicalRelationRepository.relationHasMetrics(installationId);
        } catch (MongoWriteException e) {
            throw new ConflictException(String.format("There is a Metric at {%s, %s, %s} with the following attributes : {%s, %s, %s}",
                    metric.getProject(), metric.getProvider(), metric.getInstallation(),
                    request.metricDefinitionId, request.start, request.end));
        }
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

        return hierarchicalRelationRepository.exist(projectId + HierarchicalRelation.PATH_SEPARATOR + providerId);
    }


    /**
     * This method delegates to {@link HierarchicalRelationRepository hierarchicalRelationRepository} to check if the given Provider belongs to any Project
     **
     * @param providerId The Provider ID.
     * @return if provider belongs to any project.
     */
    public boolean providerBelongsToAnyProject(String providerId){

        return hierarchicalRelationRepository.exist(providerId);
    }
}
