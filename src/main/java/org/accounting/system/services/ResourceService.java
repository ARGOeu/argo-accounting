package org.accounting.system.services;

import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.clients.responses.eoscportal.EOSCResource;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.resource.EoscResourceDto;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.mappers.ResourceMapper;

import java.util.Collections;

@ApplicationScoped
public class ResourceService{

    /**
     * Returns the Eosc Resources from the given page.
     *
     * @param page Indicates the page number.
     * @param size The number of Eosc Resources to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<EoscResourceDto> findAllEoscResources(int page, int size, UriInfo uriInfo){

//        var eoscResources = getAllEoscResources();
//
//        var partition = utility.partition(eoscResources, size);
//
//        var resources = partition.get(page) == null ? new ArrayList<EOSCResource>() : partition.get(page);

        var pageable = new MongoQuery<EOSCResource>();

        pageable.list = Collections.emptyList();
        pageable.index = page;
        pageable.size = size;
        pageable.count = 0;
        pageable.page = Page.of(page, size);

        return new PageResource<>(pageable, ResourceMapper.INSTANCE.eoscResourcesToEoscResourcesDto(pageable.list()), uriInfo);
    }
}
