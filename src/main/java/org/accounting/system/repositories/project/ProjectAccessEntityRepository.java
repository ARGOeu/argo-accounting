package org.accounting.system.repositories.project;

import com.pivovarit.function.ThrowingBiFunction;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.entities.Metric;
import org.accounting.system.entities.Project;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.projections.ProjectionQuery;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ProjectAccessEntityRepository extends AccessEntityModulator<Project, String> {

    @Inject
    ProjectAccessControlRepository projectAccessControlRepository;

    /**
     * Since a client cannot generate a new Project through Accounting System API, delegates the execution to ProjectAccessControlRepository.
     * @param id Project ID
     * @param retrieveFromOpenAire Function to retrieve a Project from OpenAire
     * @return
     */
    public Project save(String id, ThrowingBiFunction<String, ProjectClient, Project, NotFoundException> retrieveFromOpenAire){

        return projectAccessControlRepository.save(id, retrieveFromOpenAire);
    }

    public void associateProjectWithProviders(String projectId, Set<String> providerIds){

         projectAccessControlRepository.associateProjectWithProviders(projectId, providerIds);
    }

    public void dissociateProviderFromProject(String projectId, Set<String> providerIds){

            projectAccessControlRepository.dissociateProviderFromProject(projectId, providerIds);
    }

    public List<HierarchicalRelationProjection> hierarchicalStructure(final String externalId) {

        return projectAccessControlRepository.hierarchicalStructure(externalId);
    }

    public Installation saveInstallation(InstallationRequestDto request) {
        return projectAccessControlRepository.saveInstallation(request);
    }

    public boolean deleteInstallationById(String id){
        return projectAccessControlRepository.deleteInstallationById(id);
    }

    public InstallationProjection lookupInstallationById(String id){

        return projectAccessControlRepository.lookupInstallationById(id);
    }

    public Installation updateInstallation(Installation entity, ObjectId id) {

        return projectAccessControlRepository.updateInstallation(entity, id);
    }

    public ProjectionQuery<InstallationProjection> lookupInstallations(String from, String localField, String foreignField, String as, int page, int size, Class<InstallationProjection> projection) {

        return projectAccessControlRepository.lookupInstallations(from, localField, foreignField, as, page, size, projection);
    }

    public Metric assignMetric(String installationId, MetricRequestDto request) {

        return projectAccessControlRepository.assignMetric(installationId, request);
    }

    public ProjectionQuery<MetricProjection> fetchAllMetricsByProjectId(String id, int page, int size){

        return projectAccessControlRepository.fetchAllMetricsByProjectId(id, page, size);
    }

    public ProjectionQuery<MetricProjection> fetchAllMetricsUnderAProvider(String projectId, String providerId, int page, int size){

        return projectAccessControlRepository.fetchAllMetricsUnderAProvider(projectId, providerId, page, size);
    }

    public ProjectionQuery<MetricProjection> fetchAllMetricsUnderAnInstallation(String installationId, int page, int size){

       return projectAccessControlRepository.fetchAllMetricsUnderAnInstallation(installationId, page, size);
    }

    @Override
    public AccessControlModulator<Project, String> accessControlModulator() {
        return projectAccessControlRepository;
    }
}
