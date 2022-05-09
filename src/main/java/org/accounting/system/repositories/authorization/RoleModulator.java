package org.accounting.system.repositories.authorization;

import org.accounting.system.dtos.authorization.update.UpdateRoleRequestDto;
import org.accounting.system.entities.authorization.Role;
import org.accounting.system.mappers.RoleMapper;
import org.accounting.system.repositories.modulators.AbstractModulator;
import org.bson.types.ObjectId;

import javax.inject.Inject;

public class RoleModulator extends AbstractModulator<Role, ObjectId> {

    @Inject
    RoleAccessAlwaysRepository roleAccessAlwaysRepository;

    @Inject
    RoleAccessEntityRepository roleAccessEntityRepository;

    public Role updateEntity(ObjectId id, UpdateRoleRequestDto updateRoleRequestDto) {

        Role entity = findById(id);

        RoleMapper.INSTANCE.updateRoleFromDto(updateRoleRequestDto, entity);

        return super.updateEntity(entity, id);
    }

    @Override
    public RoleAccessAlwaysRepository always() {
        return roleAccessAlwaysRepository;
    }

    @Override
    public RoleAccessEntityRepository entity() {
        return roleAccessEntityRepository;
    }
}
