package org.accounting.system.repositories.client;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.entities.client.Client;

import javax.enterprise.context.ApplicationScoped;

/**
 * {@link ClientRepository This repository} encapsulates the logic required to access
 * {@link Client} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link Client} collection. It is also responsible for mapping
 * the data from the storage format to the {@link Client}.
 *
 * Since {@link ClientRepository this repository} extends {@link ClientModulator},
 * it has access to all queries, which determine the degree of accessibility of the data.
 *
 * Also, all the operations that are defined on {@link PanacheMongoRepository} are available on this repository.
 * In this repository, we essentially define the queries that will be executed on the database without any restrictions.
 */
@ApplicationScoped
public class ClientRepository extends ClientModulator {


}