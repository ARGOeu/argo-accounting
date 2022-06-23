package org.accounting.system.repositories.project;

import com.pivovarit.function.ThrowingBiFunction;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.installation.UpdateInstallationRequestDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.entities.Metric;
import org.accounting.system.entities.Project;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.projections.ProjectionQuery;
import org.accounting.system.mappers.InstallationMapper;
import org.accounting.system.mappers.ProjectMapper;
import org.accounting.system.repositories.installation.InstallationAccessAlwaysRepository;
import org.accounting.system.repositories.modulators.AbstractModulator;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class ProjectModulator extends AbstractModulator<Project, String> {


    @Inject
    ProjectAccessEntityRepository projectAccessEntityRepository;

    @Inject
    ProjectAccessAlwaysRepository projectAccessAlwaysRepository;

    @Inject
    InstallationAccessAlwaysRepository installationAccessAlwaysRepository;

    public Project save(String id){

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                 return projectAccessAlwaysRepository.save(id, given());
            case ENTITY:
                return projectAccessEntityRepository.save(id, given());
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public void associateProjectWithProviders(String projectId, Set<String> providerIds){

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                 projectAccessAlwaysRepository.associateProjectWithProviders(projectId, providerIds);
                 break;
            case ENTITY:
                 projectAccessEntityRepository.associateProjectWithProviders(projectId, providerIds);
                 break;
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public void dissociateProviderFromProject(String projectId, Set<String> providerIds){

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                projectAccessAlwaysRepository.dissociateProviderFromProject(projectId, providerIds);
                break;
            case ENTITY:
                projectAccessEntityRepository.dissociateProviderFromProject(projectId, providerIds);
                break;
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public List<HierarchicalRelationProjection> hierarchicalStructure(final String externalId) {

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return projectAccessAlwaysRepository.hierarchicalStructure(externalId);
            case ENTITY:
                return projectAccessEntityRepository.hierarchicalStructure(externalId);
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public Installation saveInstallation(InstallationRequestDto request) {
        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return projectAccessAlwaysRepository.saveInstallation(request);
            case ENTITY:
                return projectAccessEntityRepository.saveInstallation(request);
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public boolean deleteInstallationById(String id){

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return projectAccessAlwaysRepository.deleteInstallationById(id);
            case ENTITY:
                return projectAccessEntityRepository.deleteInstallationById(id);
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public InstallationProjection lookupInstallationById(String id){

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return projectAccessAlwaysRepository.lookupInstallationById(id);
            case ENTITY:
                return projectAccessEntityRepository.lookupInstallationById(id);
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public Installation updateInstallation(String id, UpdateInstallationRequestDto request) {

        Installation entity = installationAccessAlwaysRepository.findById(new ObjectId(id));

        InstallationMapper.INSTANCE.updateInstallationFromDto(request, entity);

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return projectAccessAlwaysRepository.updateInstallation(entity, new ObjectId(id));
            case ENTITY:
                return projectAccessEntityRepository.updateInstallation(entity, new ObjectId(id));
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public ProjectionQuery<InstallationProjection> lookupInstallations(String from, String localField, String foreignField, String as, int page, int size, Class<InstallationProjection> projection) {

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return projectAccessAlwaysRepository.lookupInstallations(from, localField, foreignField, as, page, size, projection);
            case ENTITY:
                return projectAccessEntityRepository.lookupInstallations(from, localField, foreignField, as, page, size, projection);
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public Metric assignMetric(String installationId, MetricRequestDto request) {

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return projectAccessAlwaysRepository.assignMetric(installationId, request);
            case ENTITY:
                return projectAccessEntityRepository.assignMetric(installationId, request);
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public ProjectionQuery<MetricProjection> fetchAllMetricsByProjectId(String id, int page, int size){

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return projectAccessAlwaysRepository.fetchAllMetricsByProjectId(id, page, size);
            case ENTITY:
                return projectAccessEntityRepository.fetchAllMetricsByProjectId(id, page, size);
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public ProjectionQuery<MetricProjection> fetchAllMetricsUnderAProvider(String projectId, String providerId, int page, int size){

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return projectAccessAlwaysRepository.fetchAllMetricsUnderAProvider(projectId, providerId, page, size);
            case ENTITY:
                return projectAccessEntityRepository.fetchAllMetricsUnderAProvider(projectId, providerId, page, size);
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public ProjectionQuery<MetricProjection> fetchAllMetricsUnderAnInstallation(String installationId, int page, int size){

        switch (getRequestInformation().getAccessType()){
            case ALWAYS:
                return projectAccessAlwaysRepository.fetchAllMetricsUnderAnInstallation(installationId, page, size);
            case ENTITY:
                return projectAccessEntityRepository.fetchAllMetricsUnderAnInstallation(installationId, page, size);
            default:
                throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    @Override
    public ProjectAccessAlwaysRepository always() {
        return projectAccessAlwaysRepository;
    }

    @Override
    public ProjectAccessEntityRepository entity() {
        return projectAccessEntityRepository;
    }

    public static ThrowingBiFunction<String, ProjectClient, Project, NotFoundException> given() {
        return (id, client) -> {
            var responseFromOpenAire = client.getById(id, "json");

            if(Objects.isNull(responseFromOpenAire.response.results)){
                throw new NotFoundException("Project with id {"+id+"} not found.");
            }

            return ProjectMapper.INSTANCE.openAireResponseToProject(responseFromOpenAire);
        };
    }
}
