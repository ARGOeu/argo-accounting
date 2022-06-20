package org.accounting.system.repositories.project;

import com.pivovarit.function.ThrowingBiFunction;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.Metric;
import org.accounting.system.entities.Project;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.projections.ProjectionQuery;
import org.accounting.system.enums.RelationType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.installation.InstallationAccessAlwaysRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.modulators.AccessAlwaysModulator;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.HierarchicalRelationService;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ProjectAccessAlwaysRepository extends AccessAlwaysModulator<Project, String> {

    @Inject
    @RestClient
    ProjectClient projectClient;

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    InstallationRepository installationRepository;

    @Inject
    InstallationAccessAlwaysRepository installationAccessAlwaysRepository;

    @Inject
    HierarchicalRelationService hierarchicalRelationService;

    public Project save(String id, ThrowingBiFunction<String, ProjectClient, Project, NotFoundException> retrieveFromOpenAire){

        findByIdOptional(id).ifPresent(project -> {throw new ConflictException("The Project with id {"+project.getId()+"} has already been registered.");});

        var project = retrieveFromOpenAire.apply(id, projectClient);

        persistOrUpdate(project);

        return project;
    }

    public void associateProjectWithProviders(String projectId, Set<String> providerIds){

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

    public void dissociateProviderFromProject(String projectId, Set<String> providerIds){

        for(String provider: providerIds){

            providerRepository.findByIdOptional(provider).orElseThrow(()->new NotFoundException("There is no Provider with the following id: "+provider));

            var installations = installationRepository.list("project = ?1 and organisation = ?2", projectId, provider);

            if(!installations.isEmpty()){
                throw new ConflictException("Dissociation is not allowed. There are registered installations to {"+projectId+", "+provider+"}.");
            }
        }

        for(String provider: providerIds){
            hierarchicalRelationRepository.delete("_id = ?1",projectId + HierarchicalRelation.PATH_SEPARATOR + provider);
        }
    }

    public Installation saveInstallation(InstallationRequestDto request) {

        installationAccessAlwaysRepository.exist(request.infrastructure, request.installation);
        return installationAccessAlwaysRepository.save(request);
    }

    public boolean deleteInstallationById(String id){
        return installationAccessAlwaysRepository.deleteEntityById(new ObjectId(id));
    }

    public InstallationProjection lookupInstallationById(String id){

        return installationRepository.lookUpEntityById("MetricDefinition", "unit_of_access", "_id", "unit_of_access", InstallationProjection.class, new ObjectId(id));
    }

    public Installation updateInstallation(Installation entity, ObjectId id) {

        if(!StringUtils.isAllBlank(entity.getInstallation(), entity.getInfrastructure())){
            installationAccessAlwaysRepository.exist(entity.getInfrastructure(), entity.getInstallation());
        }
        return installationAccessAlwaysRepository.updateEntity(entity, id);
    }

    public ProjectionQuery<InstallationProjection> lookupInstallations(String from, String localField, String foreignField, String as, int page, int size, Class<InstallationProjection> projection) {

        return installationRepository.lookup(from, localField, foreignField, as, page, size, projection);
    }

    public List<HierarchicalRelationProjection> hierarchicalStructure(final String externalId) {

        return hierarchicalRelationRepository.hierarchicalStructure(externalId);
    }

    public Metric assignMetric(String installationId, MetricRequestDto request) {

        return hierarchicalRelationService.assignMetric(installationId, request);
    }

    public ProjectionQuery<MetricProjection> fetchAllMetricsByProjectId(String id, int page, int size){

        var projection = hierarchicalRelationRepository.findByExternalId(id, page, size);

        if(projection.count == 0){
            throw new NotFoundException("No metrics added.");
        }

        return projection;
    }

    public ProjectionQuery<MetricProjection> fetchAllMetricsUnderAProvider(String projectId, String providerId, int page, int size){

        var projection = hierarchicalRelationRepository.findByExternalId(projectId + HierarchicalRelation.PATH_SEPARATOR + providerId, page, size);

        if(projection.count == 0){
            throw new NotFoundException("No metrics added.");
        }

        return projection;
    }

    public ProjectionQuery<MetricProjection> fetchAllMetricsUnderAnInstallation(String installationId, int page, int size){

        var installation = installationAccessAlwaysRepository.findById(new ObjectId(installationId));

        var projection = hierarchicalRelationRepository.findByExternalId(installation.getProject() + HierarchicalRelation.PATH_SEPARATOR + installation.getOrganisation() + HierarchicalRelation.PATH_SEPARATOR + installationId, page, size);

        if(projection.count == 0){
            throw new NotFoundException("No metrics added.");
        }

        return projection;
    }
}
