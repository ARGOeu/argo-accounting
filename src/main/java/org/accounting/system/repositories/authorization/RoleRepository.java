package org.accounting.system.repositories.authorization;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.accounting.system.entities.authorization.Permission;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.enums.Collection;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This repository {@link RoleRepository} encapsulates the logic required to access
 * the Roles stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the Role collection. Finally, is is responsible for mapping
 * the data from the storage format to the {@link Role}.
 */
@ApplicationScoped
public class RoleRepository implements PanacheMongoRepository<Role> {

    /**
     * This method returns the permissions of a role upon a specific collection
     * @param name  role name
     * @param collection collection name
     */
    public List<Permission> getRolePermissionsUponACollection(String name, Collection collection){

        return find("name = ?1", name)
                .stream()
                .map(Role::getCollectionPermission)
                .flatMap(java.util.Collection::stream)
                .filter(cp->cp.collection.equals(collection))
                .map(cp->cp.permissions)
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

    /**
     * Fetches from the database the registered Accounting System roles
     * @return A list represents the stored roles
     */
    public List<Role> fetchRoles(){

        return findAll().list();
    }
}