package org.accounting.system.repositories.project;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.pivovarit.function.ThrowingBiFunction;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.Metric;
import org.accounting.system.entities.Project;
import org.accounting.system.entities.acl.AccessControl;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.entities.projections.ProjectionQuery;
import org.accounting.system.enums.RelationType;
import org.accounting.system.enums.acl.AccessControlPermission;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.installation.InstallationAccessAlwaysRepository;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.HierarchicalRelationService;
import org.apache.commons.lang3.StringUtils;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProjectAccessControlRepository extends AccessControlModulator<Project, String> {

    @Inject
    @RestClient
    ProjectClient projectClient;

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    @Inject
    ProviderRepository providerRepository;

    @Inject
    InstallationAccessAlwaysRepository installationAccessAlwaysRepository;

    @Inject
    HierarchicalRelationService hierarchicalRelationService;

    public Project save(String id, ThrowingBiFunction<String, ProjectClient, Project, NotFoundException> retrieveFromOpenAire){

        var optional = getAccessControl(id, AccessControlPermission.ACCESS_PROJECT);

        if(optional.isPresent()){

            findByIdOptional(id).ifPresent(project -> {throw new ConflictException("The Project with id {"+project.getId()+"} has already been registered.");});

            var project = retrieveFromOpenAire.apply(id, projectClient);

            persistOrUpdate(project);

            return project;
        } else {
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public void associateProjectWithProviders(String projectId, Set<String> providerIds){

        var optional = getAccessControl(projectId, AccessControlPermission.ACCESS_PROJECT);

        if(optional.isPresent()){

            for(String providerId : providerIds){
                providerRepository.findByIdOptional(providerId).orElseThrow(()->new NotFoundException("There is no Provider with the following id: "+providerId));
            }

            for(String providerId : providerIds){

                HierarchicalRelation project = new HierarchicalRelation(projectId, RelationType.PROJECT);
                HierarchicalRelation provider = new HierarchicalRelation(providerId, project, RelationType.PROVIDER);

                hierarchicalRelationRepository.save(project, null);
                hierarchicalRelationRepository.save(provider, null);
            }
        } else {
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public void dissociateProviderFromProject(String projectId, Set<String> providerIds){

        var optional = getAccessControl(projectId, AccessControlPermission.ACCESS_PROJECT);

        if(optional.isPresent()){
                for(String provider: providerIds){
                    hierarchicalRelationRepository.delete("_id = ?1",projectId + HierarchicalRelation.PATH_SEPARATOR + provider);
                }
        } else {
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public Installation saveInstallation(InstallationRequestDto request) {

        var optional = getAccessControl(request.project, AccessControlPermission.ACCESS_PROJECT);

        if(optional.isPresent()){

            installationAccessAlwaysRepository.exist(request.infrastructure, request.installation);
            return installationAccessAlwaysRepository.save(request);
        } else {
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public boolean deleteInstallationById(String id){

        var installation = installationAccessAlwaysRepository.findById(new ObjectId(id));

        var optional = getAccessControl(installation.getProject(), AccessControlPermission.ACCESS_PROJECT);

        if(optional.isPresent()){
            return installationAccessAlwaysRepository.deleteEntityById(new ObjectId(id));
        } else {
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public InstallationProjection lookupInstallationById(String id){

        var installation = installationAccessAlwaysRepository.findById(new ObjectId(id));

        var optional = getAccessControl(installation.getProject(), AccessControlPermission.ACCESS_PROJECT);

        if(optional.isPresent()){
            return installationAccessAlwaysRepository.lookUpEntityById("MetricDefinition", "unit_of_access", "_id", "unit_of_access", InstallationProjection.class, new ObjectId(id));
        } else {
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public Installation updateInstallation(Installation entity, ObjectId id) {

        var optional = getAccessControl(entity.getProject(), AccessControlPermission.ACCESS_PROJECT);

        if(optional.isPresent()){

            if(!StringUtils.isAllBlank(entity.getInstallation(), entity.getInfrastructure())){
                installationAccessAlwaysRepository.exist(entity.getInfrastructure(), entity.getInstallation());
            }
            return installationAccessAlwaysRepository.updateEntity(entity, id);
        } else {
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public ProjectionQuery<InstallationProjection> lookupInstallations(String from, String localField, String foreignField, String as, int page, int size, Class<InstallationProjection> projection) {

        var projects = getAccessControlRepository().findAllByWhoAndCollection(getRequestInformation().getSubjectOfToken(), collection(), AccessControlPermission.ACCESS_PROJECT);

        var projectsToString = projects
                .stream()
                .map(AccessControl::getEntity)
                .collect(Collectors.toList());

        Bson bson = Aggregates.lookup(from, localField, foreignField, as);

        List<InstallationProjection> projections = getMongoCollection("Installation").aggregate(List.of(bson, Aggregates.skip(size * (page)), Aggregates.match(Filters.in("project", projectsToString)), Aggregates.limit(size)), projection).into(new ArrayList<>());

        var projectionQuery = new ProjectionQuery<InstallationProjection>();

        projectionQuery.list = projections;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = getMongoCollection().countDocuments();

        return projectionQuery;
    }

    public List<HierarchicalRelationProjection> hierarchicalStructure(final String externalId) {

        var optional = getAccessControl(externalId, AccessControlPermission.ACCESS_PROJECT);

        if(optional.isPresent()){
            return hierarchicalRelationRepository.hierarchicalStructure(externalId);
        } else {
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public Metric assignMetric(String installationId, MetricRequestDto request) {

        var installation = installationAccessAlwaysRepository.findById(new ObjectId(installationId));

        var optional = getAccessControl(installation.getProject(), AccessControlPermission.ACCESS_PROJECT);

        if(optional.isPresent()){
            return hierarchicalRelationService.assignMetric(installationId, request);
        } else {
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public ProjectionQuery<MetricProjection> fetchAllMetricsByProjectId(String id, int page, int size){

        var optional = getAccessControl(id, AccessControlPermission.ACCESS_PROJECT);

        if(optional.isPresent()){

            var projection = hierarchicalRelationRepository.findByExternalId(id, page, size);

            if(projection.count == 0){
                throw new NotFoundException("No metrics added.");
            }

            return projection;
        } else {
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public ProjectionQuery<MetricProjection> fetchAllMetricsUnderAProvider(String projectId, String providerId, int page, int size){

        var optional = getAccessControl(projectId, AccessControlPermission.ACCESS_PROJECT);

        if(optional.isPresent()){

            var projection = hierarchicalRelationRepository.findByExternalId(projectId + HierarchicalRelation.PATH_SEPARATOR + providerId, page, size);

        if(projection.count == 0){
            throw new NotFoundException("No metrics added.");
        }

        return projection;

        } else {
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }

    public ProjectionQuery<MetricProjection> fetchAllMetricsUnderAnInstallation(String installationId, int page, int size){

        var installation = installationAccessAlwaysRepository.findById(new ObjectId(installationId));

        var optional = getAccessControl(installation.getProject(), AccessControlPermission.ACCESS_PROJECT);

        if(optional.isPresent()){

        var projection = hierarchicalRelationRepository.findByExternalId(installation.getProject() + HierarchicalRelation.PATH_SEPARATOR + installation.getOrganisation() + HierarchicalRelation.PATH_SEPARATOR + installationId, page, size);

        if(projection.count == 0){
            throw new NotFoundException("No metrics added.");
        }

        return projection;
        } else {
            throw new ForbiddenException("The authenticated client is not permitted to perform the requested operation.");
        }
    }
}
