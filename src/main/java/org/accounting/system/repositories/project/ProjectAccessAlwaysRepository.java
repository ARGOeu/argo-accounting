package org.accounting.system.repositories.project;

import com.pivovarit.function.ThrowingBiFunction;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.entities.HierarchicalRelation;
import org.accounting.system.entities.Project;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.enums.RelationType;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.installation.InstallationAccessAlwaysRepository;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.modulators.AccessAlwaysModulator;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ProjectAccessAlwaysRepository extends AccessAlwaysModulator<Project, String, RoleAccessControl> {

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


    public PanacheQuery<InstallationProjection> lookupInstallations(String from, String localField, String foreignField, String as, int page, int size, Class<InstallationProjection> projection) {

        return installationAccessAlwaysRepository.lookup(from, localField, foreignField, as, page, size, projection);
    }

    public List<HierarchicalRelationProjection> hierarchicalStructure(final String externalId) {

        return hierarchicalRelationRepository.hierarchicalStructure(externalId);
    }

    public PanacheQuery<MetricProjection> fetchAllMetrics(String id, int page, int size){

        var projection = hierarchicalRelationRepository.findByExternalId(id, page, size);

        return projection;
    }

    public boolean accessibility(String projectId){

        return true;
    }
}
