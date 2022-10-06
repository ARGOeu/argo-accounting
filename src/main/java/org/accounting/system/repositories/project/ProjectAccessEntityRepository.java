package org.accounting.system.repositories.project;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.entities.Project;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ProjectAccessEntityRepository extends AccessEntityModulator<Project, String, RoleAccessControl> {

    @Inject
    ProjectAccessControlRepository projectAccessControlRepository;

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

//    /**
//     * Since a client cannot generate a new Project through Accounting System API, delegates the execution to ProjectAccessControlRepository.
//     * @param id Project ID
//     * @param retrieveFromOpenAire Function to retrieve a Project from OpenAire
//     * @return
//     */
//    public Project save(String id, ThrowingBiFunction<String, ProjectClient, Project, NotFoundException> retrieveFromOpenAire){
//
//        return projectAccessControlRepository.save(id, retrieveFromOpenAire);
//    }
//
//    public void associateProjectWithProviders(String projectId, Set<String> providerIds){
//
//         projectAccessControlRepository.associateProjectWithProviders(projectId, providerIds);
//    }
//
//    public void dissociateProviderFromProject(String projectId, Set<String> providerIds){
//
//            projectAccessControlRepository.dissociateProviderFromProject(projectId, providerIds);
//    }
//
//    public List<HierarchicalRelationProjection> hierarchicalStructure(final String externalId) {
//
//        return projectAccessControlRepository.hierarchicalStructure(externalId);
//    }
//
//
//    public ProjectionQuery<InstallationProjection> lookupInstallations(String from, String localField, String foreignField, String as, int page, int size, Class<InstallationProjection> projection) {
//
//        return projectAccessControlRepository.lookupInstallations(from, localField, foreignField, as, page, size, projection);
//    }

    public PanacheQuery<MetricProjection> fetchAllMetrics(String id, int page, int size){

        return hierarchicalRelationRepository.findByExternalId(id, page, size);
    }


    public boolean accessibility(String projectId, Collection collection, Operation operation){

        return projectAccessControlRepository.accessibility(projectId, collection, operation);
    }

    @Override
    public AccessControlModulator<Project, String, RoleAccessControl> accessControlModulator() {
        return projectAccessControlRepository;
    }
}
