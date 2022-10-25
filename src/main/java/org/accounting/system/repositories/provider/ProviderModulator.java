package org.accounting.system.repositories.provider;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.accounting.system.dtos.provider.UpdateProviderRequestDto;
import org.accounting.system.entities.projections.InstallationProjection;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.mappers.ProviderMapper;
import org.accounting.system.repositories.modulators.AbstractModulator;
import org.accounting.system.services.HierarchicalRelationService;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import java.util.Objects;


public class ProviderModulator extends AbstractModulator<Provider, String> {


    @Inject
    ProviderAccessEntityRepository providerAccessEntityRepository;

    @Inject
    ProviderAccessAlwaysRepository providerAccessAlwaysRepository;

    @Inject
    HierarchicalRelationService hierarchicalRelationService;

    /**
     * This method is responsible for updating a part or all attributes of existing Provider.
     *
     * @param id The Provider to be updated.
     * @param request The Provider attributes to be updated.
     * @return The updated Provider.
     * @throws ForbiddenException If Provider derives from EOSC-Portal.
     */
    public Provider updateEntity(String id, UpdateProviderRequestDto request) {

        Provider entity = findById(id);

        // if Provider's creator id is null then it derives from EOSC-Portal
        if(Objects.isNull(entity.getCreatorId())){
            throw new ForbiddenException("You cannot update a Provider which derives from EOSC-Portal.");
        }

        if(hierarchicalRelationService.providerBelongsToAnyProject(id)){
            throw new ForbiddenException("You cannot update a Provider which belongs to a Project.");
        }

        ProviderMapper.INSTANCE.updateProviderFromDto(request, entity);

        return super.updateEntity(entity, id);
    }

    public PanacheQuery<InstallationProjection> lookupInstallations(String from, String localField, String foreignField, String as, int page, int size, Class<InstallationProjection> projection) {

        throw new ForbiddenException("This operation has not been implemented yet.");
    }

    @Override
    public ProviderAccessAlwaysRepository always() {
        return providerAccessAlwaysRepository;
    }

    @Override
    public ProviderAccessEntityRepository entity() {
        return providerAccessEntityRepository;
    }
}
