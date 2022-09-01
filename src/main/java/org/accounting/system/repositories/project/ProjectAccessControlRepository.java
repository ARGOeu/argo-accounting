package org.accounting.system.repositories.project;

import org.accounting.system.entities.Project;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.repositories.acl.AccessControlRepository;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.services.authorization.RoleService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ProjectAccessControlRepository extends AccessControlModulator<Project, String, RoleAccessControl> {

    @Inject
    RoleService roleService;

    @Inject
    AccessControlRepository accessControlRepository;

//    public Project save(String id, ThrowingBiFunction<String, ProjectClient, Project, NotFoundException> retrieveFromOpenAire){
//
//        var optional = getAccessControl(id, AccessControlPermission.REGISTER);
//
//        if(optional.isPresent()){
//
//            findByIdOptional(id).ifPresent(project -> {throw new ConflictException("The Project with id {"+project.getId()+"} has already been registered.");});
//
//            var project = retrieveFromOpenAire.apply(id, projectClient);
//
//            persistOrUpdate(project);
//
//            return project;
//        } else {
//            throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
//        }
//    }

//    public void associateProjectWithProviders(String projectId, Set<String> providerIds){
//
//        var optional = getAccessControl(projectId, AccessControlPermission.ASSOCIATE);
//
//        if(optional.isPresent()){
//
//            for(String providerId : providerIds){
//                providerRepository.findByIdOptional(providerId).orElseThrow(()->new NotFoundException("There is no Provider with the following id: "+providerId));
//            }
//
//            for(String providerId : providerIds){
//
//                HierarchicalRelation project = new HierarchicalRelation(projectId, RelationType.PROJECT);
//                HierarchicalRelation provider = new HierarchicalRelation(providerId, project, RelationType.PROVIDER);
//
//                hierarchicalRelationRepository.save(project, null);
//                hierarchicalRelationRepository.save(provider, null);
//            }
//        } else {
//            throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
//        }
//    }

//    public void dissociateProviderFromProject(String projectId, Set<String> providerIds){
//
//        var optional = getAccessControl(projectId, AccessControlPermission.DISSOCIATE);
//
//        if(optional.isPresent()){
//                for(String provider: providerIds){
//                    hierarchicalRelationRepository.delete("_id = ?1",projectId + HierarchicalRelation.PATH_SEPARATOR + provider);
//                }
//        } else {
//            throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
//        }
//    }


//    public ProjectionQuery<InstallationProjection> lookupInstallations(String from, String localField, String foreignField, String as, int page, int size, Class<InstallationProjection> projection) {
//
//        var projects = getAccessControlRepository().findAllByWhoAndCollection(getRequestInformation().getSubjectOfToken(), collection(), AccessControlPermission.ACCESS_PROJECT);
//
//        var projectsToString = projects
//                .stream()
//                .map(AccessControl::getEntity)
//                .collect(Collectors.toList());
//
//        Bson bson = Aggregates.lookup(from, localField, foreignField, as);
//
//        List<InstallationProjection> projections = getMongoCollection("Installation").aggregate(List.of(bson, Aggregates.skip(size * (page)), Aggregates.match(Filters.in("project", projectsToString)), Aggregates.limit(size)), projection).into(new ArrayList<>());
//
//        var projectionQuery = new ProjectionQuery<InstallationProjection>();
//
//        projectionQuery.list = projections;
//        projectionQuery.index = page;
//        projectionQuery.size = size;
//        projectionQuery.count = getMongoCollection().countDocuments();
//
//        return projectionQuery;
//    }

//    public List<HierarchicalRelationProjection> hierarchicalStructure(final String externalId) {
//
//        var optional = getAccessControl(externalId, AccessControlPermission.READ);
//
//        if(optional.isPresent()){
//            return hierarchicalRelationRepository.hierarchicalStructure(externalId);
//        } else {
//            throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
//        }
//    }

//    public Metric assignMetric(String installationId, MetricRequestDto request) {
//
//        var installation = installationAccessAlwaysRepository.findById(new ObjectId(installationId));
//
//        var optional = getAccessControl(installation.getProject(), AccessControlPermission.ACCESS_PROJECT);
//
//        if(optional.isPresent()){
//            return hierarchicalRelationService.assignMetric(installationId, request);
//        } else {
//            throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
//        }
//    }

//    public ProjectionQuery<MetricProjection> fetchAllMetricsByProjectId(String id, int page, int size){
//
//        var optional = getAccessControl(id, AccessControlPermission.ACCESS_PROJECT);
//
//        if(optional.isPresent()){
//
//            var projection = hierarchicalRelationRepository.findByExternalId(id, page, size);
//
//            if(projection.count == 0){
//                throw new NotFoundException("No metrics added.");
//            }
//
//            return projection;
//        } else {
//            throw new ForbiddenException(ApiMessage.NO_PERMISSION.message);
//        }
//    }

    public boolean accessibility(String projectId, Collection collection, Operation operation){

        var accessControl= accessControlRepository.findByWhoAndCollectionAndEntity(getRequestInformation().getSubjectOfToken(), Collection.Project, projectId);

        return accessControl.filter(roleAccessControl -> roleService.hasRoleAccess(roleAccessControl.getRoles(), collection, operation)).isPresent();
    }
}