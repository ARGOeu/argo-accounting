package org.accounting.system.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.resource.ResourceResponse;
import org.accounting.system.mappers.ResourceMapper;
import org.accounting.system.repositories.ResourceRepository;

@ApplicationScoped
public class ResourceService{

    @Inject
    ResourceRepository resourceRepository;

    /**
     * Returns the Resources from the given page.
     *
     * @param page Indicates the page number.
     * @param size The number of Resources to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<ResourceResponse> findAllResources(int page, int size, UriInfo uriInfo){

        var resources = resourceRepository.findAllPageable(page, size);

        return new PageResource<>(resources, ResourceMapper.INSTANCE.resourcesToResponse(resources.list()), uriInfo);
    }

    public boolean exist(String id){

       return resourceRepository.findByIdOptional(id).isPresent();
    }
}
