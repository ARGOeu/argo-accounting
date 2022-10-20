package org.accounting.system.repositories.project;

import org.accounting.system.entities.Project;
import org.accounting.system.repositories.modulators.AccessControlModulator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectAccessControlRepository extends AccessControlModulator<Project, String> {

}