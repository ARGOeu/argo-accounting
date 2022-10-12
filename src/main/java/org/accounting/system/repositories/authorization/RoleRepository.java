package org.accounting.system.repositories.authorization;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.entities.authorization.AccessPermission;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.enums.Collection;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This repository {@link RoleRepository} encapsulates the logic required to access
 * {@link Role} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link Role} collection. It is also responsible for mapping
 * the data from the storage format to the {@link Role}.
 *
 * Since {@link RoleRepository this repository} extends {@link RoleModulator},
 * it has access to all queries, which determine the degree of accessibility of the data.
 *
 * Also, all the operations that are defined on {@link PanacheMongoRepository} are available on this repository.
 * In this repository, we essentially define the queries that will be executed on the database without any restrictions.
 */

@ApplicationScoped
public class RoleRepository extends RoleModulator {

    /**
     * This method returns the access permissions of a role upon a specific collection
     * @param name  role name
     * @param collection collection name
     */
    public List<AccessPermission> getRoleAccessPermissionsUponACollection(String name, Collection collection){

        return find("name = ?1", name)
                .stream()
                .map(Role::getCollectionsAccessPermissions)
                .flatMap(java.util.Collection::stream)
                .filter(cp->cp.collection.equals(collection))
                .map(cp->cp.accessPermissions)
                .flatMap(java.util.Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns the {@link Role} by given name
     * @param name  role name
     * @return the corresponding role
     */
    public Optional<Role> getRoleByName(String name){
        return find("name = ?1", name).stream().findFirst();
    }

    public Set<Role> getRolesByName(Set<String> names){
        return find("name in ?1", names).stream().collect(Collectors.toSet());
    }
}