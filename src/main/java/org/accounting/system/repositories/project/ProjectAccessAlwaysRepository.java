package org.accounting.system.repositories.project;

import com.pivovarit.function.ThrowingBiFunction;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.clients.ProjectClient;
import org.accounting.system.entities.Project;
import org.accounting.system.entities.acl.RoleAccessControl;
import org.accounting.system.entities.projections.HierarchicalRelationProjection;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.modulators.AccessAlwaysModulator;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class ProjectAccessAlwaysRepository extends AccessAlwaysModulator<Project, String, RoleAccessControl> {

    @Inject
    @RestClient
    ProjectClient projectClient;

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;

    public Project save(String id, ThrowingBiFunction<String, ProjectClient, Project, NotFoundException> retrieveFromOpenAire){

        findByIdOptional(id).ifPresent(project -> {throw new ConflictException("The Project with id {"+project.getId()+"} has already been registered.");});

        var project = retrieveFromOpenAire.apply(id, projectClient);

        persistOrUpdate(project);

        return project;
    }

    public List<HierarchicalRelationProjection> hierarchicalStructure(final String externalId) {

        return hierarchicalRelationRepository.hierarchicalStructure(externalId);
    }

    public PanacheQuery<MetricProjection> fetchAllMetrics(String id, int page, int size){

        var projection = hierarchicalRelationRepository.findByExternalId(id, page, size);

        return projection;
    }
}
