package org.accounting.system.repositories.provider;

import org.accounting.system.entities.provider.Provider;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

/**
 * {@link ProviderRepository This repository} encapsulates the logic required to access
 * {@link Provider} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link Provider} collection. It is also responsible for mapping
 * the data from the storage format to the {@link Provider}.
 *
 * Since {@link ProviderRepository this repository} extends {@link ProviderModulator},
 * it has access to all queries, which determine the degree of accessibility of the data.
 *
 * Also, all the operations that are defined on {@link io.quarkus.mongodb.panache.PanacheMongoRepository} are available on this repository.
 * In this repository, we essentially define the queries that will be executed on the database without any restrictions.
 */
@ApplicationScoped
public class ProviderRepository extends ProviderModulator {

    /**
     * Executes a query in mongo database to fetch a Provider by given name.
     *
     * @param name The Provider name.
     * @return the Provider wrapped in an {@link Optional}.
     */
    public Optional<Provider> findByName(String name){

        return find("name = ?1", name).stream().findAny();
    }
}
