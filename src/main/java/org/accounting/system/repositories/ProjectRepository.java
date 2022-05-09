package org.accounting.system.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import org.accounting.system.entities.Project;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectRepository implements PanacheMongoRepositoryBase<Project, String> {

}
