package org.accounting.system.repositories.project;

import org.accounting.system.entities.Project;
import org.accounting.system.repositories.modulators.AccessibleModulator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectAccessEntityRepository extends AccessibleModulator<Project, String> {

}
