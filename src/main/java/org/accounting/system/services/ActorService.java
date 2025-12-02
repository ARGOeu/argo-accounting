package org.accounting.system.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.beans.RequestUserContext;
import org.accounting.system.dtos.ActorDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.actor.Actor;
import org.accounting.system.mappers.ActorMapper;
import org.accounting.system.repositories.groupmanagement.ActorRepository;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.Date;

@ApplicationScoped
public class ActorService {

    @Inject
    ActorRepository actorRepository;

    @Inject
    RequestUserContext requestUserContext;

    public void register(){

        var dbClient = actorRepository.findByOidcIdAndIssuer(requestUserContext.getId(), requestUserContext.getIssuer());

        if(dbClient.isPresent()){

            return;
        } else {

            var actor = new Actor();

            actor.setId(new ObjectId().toString());
            actor.setIssuer(requestUserContext.getIssuer());
            actor.setName(requestUserContext.getName().orElse(""));
            actor.setEmail(requestUserContext.getEmail().orElse(""));
            actor.setRegisteredOn(LocalDateTime.now());
            actor.setOidcId(requestUserContext.getId());

            actorRepository.persist(actor);
        }
    }

    public PageResource<ActorDto> getAll(int page, int size, UriInfo uriInfo) {

        var projectionQuery = actorRepository.fetchAll(page, size);

        return new PageResource<>(projectionQuery, ActorMapper.INSTANCE.actorsToListDto(projectionQuery.list()), uriInfo);
    }

    public PageResource<ActorDto> getAllActorsEntitlements(int page, int size, UriInfo uriInfo) {

        var projectionQuery = actorRepository.fetchAll(page, size);

        return new PageResource<>(projectionQuery, ActorMapper.INSTANCE.actorsToListDto(projectionQuery.list()), uriInfo);
    }

    public long countDocuments(Date start, Date end) {

        return actorRepository.countDocuments(start, end);
    }
}
