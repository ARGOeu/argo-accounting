package org.accounting.system.services;

import io.quarkus.cache.CacheResult;
import io.quarkus.panache.common.Page;
import org.accounting.system.clients.ResourceClient;
import org.accounting.system.clients.responses.eoscportal.EOSCResource;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.resource.EoscResourceDto;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.mappers.ResourceMapper;
import org.accounting.system.util.Utility;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ResourceService{

    @Inject
    Utility utility;

    @Inject
    @RestClient
    ResourceClient resourceClient;

    @Inject
    Logger LOG;

    /**
     * Returns the Eosc Resources from the given page.
     *
     * @param page Indicates the page number.
     * @param size The number of Eosc Resources to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<EoscResourceDto> findAllEoscResources(int page, int size, UriInfo uriInfo){

        var eoscResources = getAllEoscResources();

        var partition = utility.partition(eoscResources, size);

        var resources = partition.get(page) == null ? new ArrayList<EOSCResource>() : partition.get(page);

        var pageable = new MongoQuery<EOSCResource>();

        pageable.list = resources;
        pageable.index = page;
        pageable.size = size;
        pageable.count = eoscResources.size();
        pageable.page = Page.of(page, size);

        return new PageResource<>(pageable, ResourceMapper.INSTANCE.eoscResourcesToEoscResourcesDto(pageable.list()), uriInfo);
    }

    /**
     * Communicates with EOSC Portal to retrieve the available EOSC Resources.
     *
     * @return The available Eosc Resources.
     * @throws InternalServerErrorException Error communication with Eosc Provider Portal.
     * @throws NotFoundException Cannot found the Eosc Resources.
     */
    @CacheResult(cacheName = "eosc-resources")
    public List<EOSCResource> getAllEoscResources(){

        LOG.info("Retrieving the total number of EOSC Resources.");

        try{
            var total = resourceClient.getTotalNumberOfResources("all");

            LOG.infof("There are %s Resources in EOSC Portal.", total.total);
            LOG.info("Retrieving the available Resources from EOSC-Portal.");

            var response = resourceClient.getAll("all", total.total);
            return response.results;
        } catch (ValidationException e){
            throw new NotFoundException("Cannot found the Eosc Resources.");
        } catch (Exception re){
            throw new InternalServerErrorException("Failed to communicate with EOSC-Portal.");
        }
    }

    public boolean exist(String id){

        return getAllEoscResources().stream().anyMatch(resource->resource.id.equals(id));
    }
}
