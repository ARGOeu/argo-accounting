package org.accounting.system.repositories.project;

import jakarta.enterprise.context.ApplicationScoped;
import org.accounting.system.entities.Project;
import org.accounting.system.repositories.modulators.AccessibleModulator;

@ApplicationScoped
public class ProjectAccessEntityRepository extends AccessibleModulator<Project, String> {

}
