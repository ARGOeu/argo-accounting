package org.accounting.system.services.groupmanagement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.ActorEntitlementsDto;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.mappers.ActorEntitlementsMapper;
import org.accounting.system.repositories.groupmanagement.ActorEntitlementsRepository;

@ApplicationScoped
public class ActorEntitlementsService {

    @Inject
    ActorEntitlementsRepository actorEntitlementsRepository;

    public PageResource<ActorEntitlementsDto> getAll(int page, int size, UriInfo uriInfo) {

        var projectionQuery = actorEntitlementsRepository.fetchAll(page, size);

        return new PageResource<>(projectionQuery, ActorEntitlementsMapper.INSTANCE.actorsEntitlementsToDto(projectionQuery.list()), uriInfo);
    }
}
