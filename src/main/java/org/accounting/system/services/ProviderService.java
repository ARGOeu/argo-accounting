package org.accounting.system.services;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.dtos.PageResource;
import org.accounting.system.dtos.provider.ProviderResponseDto;
import org.accounting.system.entities.Provider;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.ProviderRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;

@ApplicationScoped
public class ProviderService {

    @Inject
    ProviderRepository providerRepository;


    /**
     * Returns the N Providers from the given page.
     *
     * @param page Indicates the page number.
     * @param size The number of Metrics to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<Provider, ProviderResponseDto> findAllProvidersPageable(int page, int size, String id, UriInfo uriInfo){

        PanacheQuery<Provider> panacheQuery = providerRepository.findAllProvidersPageable(page, size, id);

        if(panacheQuery.list().isEmpty()){
            throw new NotFoundException("Not Found.");
        }

        return new PageResource<>(panacheQuery, ProviderMapper.INSTANCE.providersToResponse(panacheQuery.list()), uriInfo);
    }

}
