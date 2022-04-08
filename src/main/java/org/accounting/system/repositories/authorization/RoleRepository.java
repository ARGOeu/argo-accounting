package org.accounting.system.repositories.authorization;

import org.accounting.system.dtos.authorization.update.UpdateRoleRequestDto;
import org.accounting.system.entities.authorization.Permission;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.enums.Collection;
import org.accounting.system.mappers.RoleMapper;
import org.accounting.system.repositories.modulators.AbstractModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This repository {@link RoleRepository} encapsulates the logic required to access
 * {@link Role} data stored in the mongo database. More specifically, it encapsulates the queries
 * that can be performed on the {@link Role} collection. It is also responsible for mapping
 * the data from the storage format to the {@link Role}.
 *
 * Since {@link RoleRepository} extends {@link AbstractModulator},
 * it has to provide it with the corresponding {@link org.accounting.system.repositories.modulators.AccessModulator} implementations.
 * Also, all the operations that are defined on {@link io.quarkus.mongodb.panache.PanacheMongoRepository} and on
 * {@link org.accounting.system.repositories.modulators.AccessModulator} are available on this repository.
 */
@ApplicationScoped
public class RoleRepository extends RoleModulator {

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

    public Role updateEntity(ObjectId id, UpdateRoleRequestDto updateRoleRequestDto) {

        Role entity = findById(id);

        RoleMapper.INSTANCE.updateRoleFromDto(updateRoleRequestDto, entity);

        return super.updateEntity(entity);
    }
}