package org.accounting.system.repositories.project;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.entities.Project;
import org.accounting.system.entities.projections.MetricProjection;
import org.accounting.system.repositories.HierarchicalRelationRepository;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ProjectAccessEntityRepository extends AccessEntityModulator<Project, String> {

    @Inject
    ProjectAccessControlRepository projectAccessControlRepository;

    @Inject
    HierarchicalRelationRepository hierarchicalRelationRepository;


    public PanacheQuery<MetricProjection> fetchAllMetrics(String id, int page, int size){

        return hierarchicalRelationRepository.findByExternalId(id, page, size);
    }

    @Override
    public AccessControlModulator<Project, String> accessControlModulator() {
        return projectAccessControlRepository;
    }
}
