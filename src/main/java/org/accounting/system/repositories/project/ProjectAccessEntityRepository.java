package org.accounting.system.repositories.project;

import org.accounting.system.entities.Project;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.accounting.system.repositories.modulators.AccessEntityModulator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ProjectAccessEntityRepository extends AccessEntityModulator<Project, String> {

    @Inject
    ProjectAccessControlRepository projectAccessControlRepository;

    @Override
    public AccessControlModulator<Project, String> accessControlModulator() {
        return projectAccessControlRepository;
    }
}
