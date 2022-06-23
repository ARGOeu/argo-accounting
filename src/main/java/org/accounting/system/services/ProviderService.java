package org.accounting.system.services;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.dtos.provider.ProviderRequestDto;
import org.accounting.system.dtos.provider.ProviderResponseDto;
import org.accounting.system.dtos.provider.UpdateProviderRequestDto;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.exceptions.ConflictException;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.UriInfo;
import java.util.Objects;

@ApplicationScoped
public class ProviderService {

    @Inject
    ProviderRepository providerRepository;


    /**
     * Returns the N Providers from the given page.
     *
     * @param page Indicates the page number.
     * @param size The number of Providers to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    public PageResource<Provider, ProviderResponseDto> findAllProvidersPageable(int page, int size, UriInfo uriInfo){

        PanacheQuery<Provider> panacheQuery = providerRepository.findAllPageable(page, size);

        return new PageResource<>(panacheQuery, ProviderMapper.INSTANCE.providersToResponse(panacheQuery.list()), uriInfo);
    }

    /**
     * Maps the {@link ProviderRequestDto} to {@link Provider}.
     * Then the {@link Provider} is stored in the mongo database.
     *
     * @param request The POST request body.
     * @return The stored Provider has been turned into a response body.
     */
    public ProviderResponseDto save(ProviderRequestDto request) {

        var provider = ProviderMapper.INSTANCE.requestToProvider(request);

        providerRepository.persist(provider);

        return ProviderMapper.INSTANCE.providerToResponse(provider);
    }

    /**
     * Checks if a Provider with given id exists.
     *
     * @param id The Provider id.
     * @throws ConflictException If Provider already exists.
     */
    public void existById(String id){

        providerRepository.findByIdOptional(id)
                .ifPresent(provider -> {throw new ConflictException("There is a Provider with id: "+id);});
    }

    /**
     * Checks if a Provider with given name exists.
     *
     * @param name The Provider name.
     * @throws ConflictException If Provider already exists.
     */
    public void existByName(String name){

        providerRepository.findByName(name)
                .ifPresent(provider -> {throw new ConflictException("There is a Provider with name: "+name);});
    }

    /**
     * Delete a Provider by given id.
     * @param providerId The Provider to be deleted.
     * @return If the operation is successful or not.
     * @throws ForbiddenException If the Providers derives from EOSC-Portal.
     */
    public boolean delete(String providerId){

        var provider = providerRepository.findById(providerId);

        // if Provider's creator id is null then it derives from EOSC-Portal
        if(Objects.isNull(provider.getCreatorId())){
            throw new ForbiddenException("You cannot delete a Provider which derives from EOSC-Portal.");
        }

        return providerRepository.deleteEntityById(providerId);
    }

    /**
     * This method is responsible for checking if there is a Provider with given name or id.
     * Then, it calls the {@link ProviderRepository providerRepository} to update a Provider.
     *
     * @param id The Provider to be updated.
     * @param request The Provider attributes to be updated.
     * @return The updated Provider.
     * @throws ConflictException If Provider with the given name or id already exists.
     */
    public ProviderResponseDto update(String id, UpdateProviderRequestDto request){

        if(StringUtils.isNotEmpty(request.id)){
            existById(request.id);
        }

        if(StringUtils.isNotEmpty(request.name)){
            existByName(request.name);
        }

        var provider = providerRepository.updateEntity(id, request);

        return ProviderMapper.INSTANCE.providerToResponse(provider);
    }

    /**
     * Fetches a Provider by given id.
     *
     * @param id The Provider id.
     * @return The corresponding Provider.
     */
    public ProviderResponseDto fetchProvider(String id){

        var provider = providerRepository.fetchEntityById(id);

        return ProviderMapper.INSTANCE.providerToResponse(provider);
    }

    /**
     * This method check whether the given Provider derived from EOSC-Portal or not.
     *
     * @param id The Provider ID.
     * @throws ForbiddenException If provider derives from EOSC-Portal
     */
    public void derivesFromEoscPortal(String id){

        Provider entity = providerRepository.findById(id);

        // if Provider's creator id is null then it derives from EOSC-Portal
        if(Objects.isNull(entity.getCreatorId())){
            throw new ForbiddenException("You cannot access a Provider which derives from EOSC-Portal.");
        }
    }

}
