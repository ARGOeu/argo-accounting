package org.accounting.system.repositories.installation;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.entities.installation.Installation;

import javax.enterprise.context.ApplicationScoped;

/**
 * {@link InstallationRepository This repository} encapsulates the logic required to access
 * {@link Installation} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link Installation} collection. It is also responsible for mapping
 * the data from the storage format to the {@link Installation}.
 *
 * Since {@link InstallationRepository this repository} extends {@link InstallationModulator},
 * it has access to all queries, which determine the degree of accessibility of the data.
 *
 * Also, all the operations that are defined on {@link PanacheMongoRepository} are available on this repository.
 * In this repository, we essentially define the queries that will be executed on the database without any restrictions.
 */
@ApplicationScoped
public class InstallationRepository extends InstallationModulator {


}