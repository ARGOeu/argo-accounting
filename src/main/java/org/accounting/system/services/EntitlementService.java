package org.accounting.system.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.entitlement.EntitlementDto;
import org.accounting.system.dtos.entitlement.EntitlementRequest;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.actor.ActorEntitlements;
import org.accounting.system.entities.actor.Entitlement;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.EntitlementMapper;
import org.accounting.system.repositories.groupmanagement.ActorEntitlementsRepository;
import org.accounting.system.repositories.groupmanagement.EntitlementRepository;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@ApplicationScoped
public class EntitlementService {

    @Inject
    EntitlementRepository entitlementRepository;

    @Inject
    ActorEntitlementsRepository actorEntitlementsRepository;

    public PageResource<EntitlementDto> getAll(int page, int size, UriInfo uriInfo) {

        var projectionQuery = entitlementRepository.fetchAll(page, size);

        return new PageResource<>(projectionQuery, EntitlementMapper.INSTANCE.entitlementsToListDto(projectionQuery.list()), uriInfo);
    }

    public EntitlementDto save(EntitlementRequest request){

        var optional = entitlementRepository.findByName(request.name);

        if(optional.isPresent()){

            throw new ConflictException("There is an entitlement with name : "+request.name);
        }

        var entitlement = new Entitlement();
        entitlement.setName(request.name);
        entitlement.setRegisteredOn(LocalDateTime.now());
        entitlement.setId(new ObjectId().toString());
        entitlementRepository.persist(entitlement);
        return EntitlementMapper.INSTANCE.entitlementToDto(entitlement);
    }

    public EntitlementDto get(String id){

        return EntitlementMapper.INSTANCE.entitlementToDto(entitlementRepository.findById(id));
    }

    public EntitlementDto update(EntitlementRequest request, String id) {

        var entity = entitlementRepository.findById(id);

        if (!request.name.equals(entity.getName()) && entitlementRepository.findByName(request.name).isPresent()) {

            throw new ConflictException("Entitlement's name already in use.");
        }

        entity.setName(request.name);
        entitlementRepository.update(entity);

        return EntitlementMapper.INSTANCE.entitlementToDto(entity);
    }

    public void delete(String id){

        var isUsed = actorEntitlementsRepository.findAnyEntitlement(id);

        if(isUsed){

            throw new ForbiddenException("You cannot delete this entitlement cause is assigned to an actor!");
        }

        entitlementRepository.deleteById(id);
    }

    public void assignEntitlementToActor(String entitlementId, String actorId){

        if(actorEntitlementsRepository.findByEntitlementAndActor(entitlementId, actorId).isPresent()){

            throw new ConflictException("Entitlement already assigned to actor.");
        }

        var actorEntitlement = new ActorEntitlements();
        actorEntitlement.setEntitlementId(entitlementId);
        actorEntitlement.setActorId(actorId);
        actorEntitlement.setAssignedAt(LocalDateTime.now());
        actorEntitlement.setId(new ObjectId().toString());
        actorEntitlementsRepository.persist(actorEntitlement);
    }

    public void dissociateEntitlementFromActor(String entitlementId, String actorId){

        var optional = actorEntitlementsRepository.findByEntitlementAndActor(entitlementId, actorId);

        if(optional.isPresent()){

            actorEntitlementsRepository.delete(optional.get());
        } else {

            throw new BadRequestException("There is no entitlement assigned to actor.");
        }
    }
}
